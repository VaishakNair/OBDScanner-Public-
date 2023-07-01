package `in`.v89bhp.obdscanner.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.enums.HandlerMessageCodes
import `in`.v89bhp.obdscanner.obdparameters.SupportedPidsHolder
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

object ElmHelper {


    private const val TAG = "ElmHelper"

    private var mHandler: Handler? = null

    private var viewModelHandler: Handler? = null

    private var connectedThread: ConnectedThread? = null

    private lateinit var savedCommand: String

    private var noDataCount = 0

    lateinit var applicationContext: Context

    /**
     * Set by ***LiveDataService*** when it exits. If this is true, other handlers
     * should ignore the first response because it might have been requested by the ***LiveDataService***
     * but will be delivered to the current handler resulting in an exception. Refer 'IMReadinessSinceDtcClearedViewModel'
     * for response ignoring logic.
     */
    var liveDataServiceJustExited = false

    /**
     * Stores the last command (without '\r' suffix) that was sent to ELM327
     */
    lateinit var lastCommand: String

    /**
     * Boolean indicating whether the sequence of AT commands to initialize ELM327 has been
     * executed or not.
     */
    val elmInitialized = MutableLiveData<Boolean>(false)


    /**
     * Boolean indicating whether ECU is online and has been initialized or not. ECU is initialized by
     * issuing the 0100 command which must be supported by all ECUs.
     */
    private val _ecuInitialized = MutableLiveData<Boolean>(false)
    val ecuInitialized: LiveData<Boolean>
        get() = _ecuInitialized

    /**
     * Send a single command to ELM327 and return the response. The command
     * should be terminated with a carriage return ('\r') character.
     */
    fun send(viewModelHandler: Handler, command: String) {
        this.viewModelHandler = viewModelHandler
        this.mHandler = viewModelHandler

        if (connectedThread == null || connectedThread!!.isAlive.not()) {
            Log.i(TAG, "Starting new receiver thread.")
            connectedThread = ConnectedThread().apply { start() }
        }

        savedCommand = command

        when {
            elmInitialized.value!!.not() -> {
                mHandler = elmInitializationHandler
                connectedThread!!.send("ATPPFFOFF\r")// Turn off programmable parameters set by user or
                // any other app. This is the first command in the
                // initiation sequence chain of commands
            }

            _ecuInitialized.value!!.not() -> {
                ecuInitializationHandler.reset()
                mHandler = ecuInitializationHandler
                connectedThread!!.send("0100\r")// Initializes ECU and fetches supported PIDs for service $01
            }

            else -> connectedThread!!.send(command)
        }
    }

    /**
     * Kill the thread listening for response from ELM327. This is done by the thread
     * by closing the bluetooth socket on which it is listening.
     */
    fun stop() {
        connectedThread?.let {
            if (it.isAlive) {
                it.cancel()
            }
        }
    }

    /**
     * Thread using a *connected* bluetooth socket to send commands to ELM (non-blocking) and
     * to listen for responses from ELM (blocking)
     *
     */

    private class ConnectedThread : Thread() {
        private var inputStream: InputStream? = null
        private var outputStream: OutputStream? = null
        private val bluetoothSocket = BluetoothHelper.socket
        private val byteBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream
        private val stringBuilder = StringBuilder()
        private val utf8Charset = Charset.forName("utf-8")

        init {
            bluetoothSocket?.let {
                inputStream = it.inputStream
                outputStream = it.outputStream
            }
        }

        override fun run() {
            if (inputStream == null) {
                transmissionError(
                    applicationContext.getString(R.string.connection_error),
                    resetInitSequence = true
                )
                return
            }

            while (true) {
                val numBytesRead = try {
                    inputStream?.read(byteBuffer)

                } catch (ex: IOException) {
                    transmissionError(
                        applicationContext.getString(R.string.obd_error_1),
                        resetInitSequence = true
                    )
                    return
                } catch (ex: NullPointerException) {
                    transmissionError(
                        applicationContext.getString(R.string.obd_error_1),
                        resetInitSequence = true
                    )
                    return
                }

                val byteArray = byteBuffer.copyOfRange(0, numBytesRead as Int)
                val response = byteArray.toString(utf8Charset)
                Log.i(
                    TAG,
                    "Partial response:---$response--- Carriage return:${response.contains('\r')}"
                )
                stringBuilder.append(response)
                if (response.contains('>')) {// End of response reached
                    Log.i(TAG, "End of response reached. Joined response:$stringBuilder")

                    if (stringBuilder.contains(Utilities.NO_DATA, true)) {
                        noDataCount++
                        if (noDataCount > Utilities.NO_DATA_COUNT_THRESHOLD) {// ECU is not returning any response. Rerun ELM327 initialization sequence
                            noDataCount = 0
                            transmissionError(
                                applicationContext.getString(R.string.ign_off_error),
                                HandlerMessageCodes.MESSAGE_ERROR_IGNITION_OFF.ordinal,
                                resetInitSequence = true
                            )
                            return
                        }
                    } else {
                        noDataCount = 0
                    }
                    when {
                        stringBuilder.contains(Utilities.UNABLE_TO_CONNECT, true) -> {
                            transmissionError(
                                applicationContext.getString(R.string.ign_off_error),
                                HandlerMessageCodes.MESSAGE_ERROR_IGNITION_OFF.ordinal,
                                resetInitSequence = false
                            )
                            stringBuilder.clear()
                            return
                        }

                        stringBuilder.contains(Utilities.BUFFER_FULL, true) ||
                                stringBuilder.contains(Utilities.BUS_ERROR, true) ||
                                stringBuilder.contains(Utilities.CAN_ERROR, true) ||
                                stringBuilder.contains(Utilities.DATA_ERROR, true) -> {
                            transmissionError(
                                applicationContext.getString(R.string.non_critical_error),
                                HandlerMessageCodes.MESSAGE_ERROR_IGNORABLE.ordinal,
                                resetInitSequence = false
                            )
                            stringBuilder.clear()
                            return
                        }

                        stringBuilder.contains(Utilities.BUS_BUSY, true) -> {
                            transmissionError(
                                applicationContext.getString(R.string.obd_busy_error),
                                HandlerMessageCodes.MESSAGE_ERROR_BUS_BUSY.ordinal,
                                resetInitSequence = false
                            )
                            stringBuilder.clear()
                            return
                        }
                    }

                    // Send the response back to view model:
                    val readMsg =
                        mHandler?.obtainMessage(// TODO ',' other scaling factors PID commands below:
                            when (lastCommand) {
                                ScalingFactors.PID_FOUR_F, ScalingFactors.PID_FIVE_ZERO -> HandlerMessageCodes.MESSAGE_RESPONSE_SCALING_FACTORS.ordinal
                                "0100", "0200" -> HandlerMessageCodes.MESSAGE_RESPONSE_INITIALIZATION.ordinal
                                "ATDPN", "ATI" -> HandlerMessageCodes.MESSAGE_RESPONSE_AT_COMMAND.ordinal
                                "0902" -> HandlerMessageCodes.MESSAGE_RESPONSE_VIN.ordinal
                                else -> HandlerMessageCodes.MESSAGE_RESPONSE.ordinal
                            },
                            -1, -1,
                            stringBuilder.dropLast(1).toString()
                                .removePrefix("SEARCHING...\r")// Remove the trailing '>' and
                            // 'SEARCHING...\r' prefix that is returned when ELM327 searches for a OBD-II protocol for the
                            // vehicle before finding and fixing one.
                        )
                    readMsg?.sendToTarget()
                    stringBuilder.clear()
                }
            }
        }

        fun send(command: String) {
            lastCommand = command.dropLast(1)// Remove the '\r' suffix
            try {
                Log.i(TAG, "Sending command $command to ELM327\n Thread is alive: $isAlive")
                outputStream?.write(command.toByteArray()) ?: throw IOException()

            } catch (ex: IOException) {
                Log.i(TAG, "Exception during write", ex)
                transmissionError(
                    applicationContext.getString(R.string.sending_failed),
                    resetInitSequence = false
                )
            } catch (ex: NullPointerException) {
                Log.i(TAG, "Exception during write NPE", ex)
                transmissionError(
                    applicationContext.getString(R.string.sending_failed),
                    resetInitSequence = false
                )
            }

        }

        fun cancel() {
            try {
                bluetoothSocket?.close() // This will throw an in the ConnectedThread 'inputStream.read()' blocking call thereby stopping the thread.
            } catch (e: IOException) {
                Log.i(TAG, "close() of connect socket failed", e)
            }

        }
    }

    @Synchronized
    private fun transmissionError(
        errorMessage: String,
        arg1: Int = -1,
        resetInitSequence: Boolean
    ) {
        // Send a failure message back to the view model
        Log.i(TAG, "Connection Error")

        connectedThread = null

        if (resetInitSequence) {
            elmInitialized.postValue(false)
        }

        _ecuInitialized.postValue(false)
        SupportedPidsHolder.clear()

        val msg = mHandler?.obtainMessage(
            HandlerMessageCodes.MESSAGE_ERROR.ordinal,
            arg1,
            -1,
            errorMessage
        )
        mHandler?.sendMessage(msg!!)
    }

    @SuppressLint("HandlerLeak")
    private val elmInitializationHandler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            if (msg == null) {// TODO We are force-casting the message passed into this handler from msg? to msg!!
                // Might need logic to handle null messages. Haven't encountered any so far.
            }

            val response: String = msg?.obj as String
            when (msg?.what) {
                HandlerMessageCodes.MESSAGE_ERROR.ordinal -> {
                    val viewModelMsg =
                        viewModelHandler?.obtainMessage(
                            HandlerMessageCodes.MESSAGE_ERROR.ordinal,
                            -1,
                            -1,
                            response
                        )

                    viewModelHandler?.sendMessage(viewModelMsg!!)
                }

                HandlerMessageCodes.MESSAGE_RESPONSE.ordinal -> {
                    when {

                        lastCommand.contains("ATPPFFOFF") -> {
                            connectedThread!!.send("ATZ\r") // Reset the chip.
                        }

                        lastCommand.contains("ATZ") -> {
                            connectedThread!!.send("ATD\r")// Reset the ELM327 to default values.
                        }

                        lastCommand.contains("ATD") -> {
                            connectedThread!!.send("ATSP0\r")// Select OBD protocol automatically
                        }

                        lastCommand.contains("ATSP0") -> {
                            connectedThread!!.send("ATE0\r")// Turn off command echo
                        }

                        lastCommand.contains("ATE0") -> {
                            elmInitialized.value =
                                true // ELM initialization complete, start ECU initialization
                            ecuInitializationHandler.reset()
                            mHandler = ecuInitializationHandler
                            connectedThread!!.send("0100\r")// Fetch PIDs supported by service 01. This should be supported by all ECUs
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private val ecuInitializationHandler = object : Handler(Looper.getMainLooper()) {
        private lateinit var ecuLines: MutableList<String>
        private var ecuLinesIndex: Int = 0
        private val supportedPids: MutableSet<String> = mutableSetOf()
        private var nextSupported = 0

        override fun handleMessage(msg: Message) {
            if (msg == null) {// TODO We are force-casting the message passed into this handler from msg? to msg!!
                                // Might need logic to handle null messages. Haven't encountered any so far.
            }

            val response: String = msg?.obj as String
            when (msg?.what) {
                HandlerMessageCodes.MESSAGE_ERROR.ordinal -> {
                    val viewModelMsg =
                        viewModelHandler?.obtainMessage(
                            HandlerMessageCodes.MESSAGE_ERROR.ordinal,
                            msg.arg1,
                            -1,
                            response
                        )

                    viewModelHandler?.sendMessage(viewModelMsg!!)
                }


                HandlerMessageCodes.MESSAGE_RESPONSE_INITIALIZATION.ordinal -> {
                    Log.i(TAG, "Initialization Response >$response<")

                    val _response = response.filter {
                        // Remove spaces
                        it != ' '
                    }

                    if (_response.contains("NODATA").not() && _response.startsWith("7F")
                            .not()
                    ) {// ECU is online and has been initialized
                        // Parse and store supported PIDs of service $01. It might be a multiline response.
                        ecuLines = Utilities.splitMultilineResponse(_response)

                        addSupportedPids()
                    } else {
                        sendIgnitionOffMessageToViewModelHandler(response)
                    }
                }


                HandlerMessageCodes.MESSAGE_RESPONSE.ordinal -> {
                    Log.i(TAG, "Message Response >$response<")

                    val _response = response.filter {
                        // Remove spaces
                        it != ' '
                    }

                    if (_response.contains("NODATA").not() && _response.startsWith("7F")
                            .not()
                    ) {// ECU is online and has been initialized
                        // Parse and store supported PIDs of service $01. It might be a multiline response.
                        for (ecuLine in Utilities.splitMultilineResponse(_response)) {//
                            if (ecuLines.contains(ecuLine).not()) {
                                ecuLines.plusAssign(ecuLine)
                            }
                        }
                        addSupportedPids()
                    } else {
                        sendIgnitionOffMessageToViewModelHandler(response)
                    }
                }
            }
        }

        private fun addSupportedPids() {
            val ecuLine = ecuLines[ecuLinesIndex++]
            Log.i(TAG, "Ecu lines: $ecuLines. Current ecu line: $ecuLine")

            nextSupported = Utilities.decodeSupportedPIDs(
                ecuLine,
                Integer.parseInt(ecuLine.substring(2, 4), 16),
                supportedPids
            )
            Log.i(
                TAG,
                "Supported PIDs by 0100: $supportedPids. Next supported (Int): $nextSupported"
            )

            if (nextSupported == 0) {
                if (ecuLinesIndex < ecuLines.size) {// Process PIDs supported by the next ECU
                    addSupportedPids()
                } else {
                    SupportedPidsHolder.ecuList.add(
                        SupportedPidsHolder.EcuSupportedPids(
                            "01",
                            "ECU#1",
                            supportedPids
                        )
                    )
                    _ecuInitialized.value = true // ECU is online. Switch to view model handler
                    // and execute the original command that was sent
                    mHandler = viewModelHandler
                    connectedThread!!.send(savedCommand)
                }
            } else {
                connectedThread!!.send("01${"%02x".format(nextSupported)}\r")
            }
        }

        private fun sendIgnitionOffMessageToViewModelHandler(response: String) {
            val viewModelMsg =
                viewModelHandler?.obtainMessage(
                    HandlerMessageCodes.MESSAGE_ERROR.ordinal,
                    HandlerMessageCodes.MESSAGE_ERROR_IGNITION_OFF.ordinal,
                    -1,
                    response
                )

            viewModelHandler?.sendMessage(viewModelMsg!!)
        }

        fun reset() {
            ecuLinesIndex = 0
            supportedPids.clear()
            nextSupported = 0
        }
    }

}
