package `in`.v89bhp.obdscanner.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import `in`.v89bhp.obdscanner.enums.HandlerMessageCodes
import `in`.v89bhp.obdscanner.helpers.ElmHelper
import `in`.v89bhp.obdscanner.helpers.ScalingFactors
import `in`.v89bhp.obdscanner.helpers.Utilities
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder.parameterCount
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder.parameterList
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder.parameterRemoved

class LiveDataService : Service() {

    private var currentIndex: Int = 0

    private val _isError: MutableLiveData<Boolean> = MutableLiveData(false)

    val isError: LiveData<Boolean>
        get() = _isError

    var stopSending: Boolean = false

    var errorMessage: String? = null

    private var destroyed = false

    /**
     * Used to send messages back to the client of this bound service.
     */
    private var clientMessenger: Messenger? = null

    private val binder: LocalBinder = LocalBinder()

    override fun onCreate() {
        ElmHelper.applicationContext = applicationContext
    }

    override fun onBind(intent: Intent?): IBinder? = binder

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): LiveDataService = this@LiveDataService
    }


    /**
     * Register a client messenger to receive messages back from this service.
     */
    fun registerClientMessenger(messenger: Messenger) {
        clientMessenger = messenger
    }

    /**
     * Unregister client messenger registered using <code>registerClientMessenger()</code> method.
     */
    fun unregisterClientMessenger() {
        clientMessenger = null
    }

    /**
     * Request current powertrain diagnostic data (Service 01 of SAEJ1979 specification)
     */
    fun sendPid() {
        stopSending = false
        _isError.value = false

        if(!ScalingFactors.initialized) {// Scaling factors for some PIDs have not been initialized.Initialize them
            ElmHelper.send(mHandler, "${ScalingFactors.PID_FOUR_F}\r")
            return
        }

        when {
            parameterCount == 0 -> return

            currentIndex < parameterCount -> ElmHelper.send(mHandler, "01${parameterList[currentIndex].pid}")

            currentIndex == parameterCount -> {// End of parameter list reached. Re-start from the first parameter:
                currentIndex = 0
                sendPid()
            }
        }
    }

    /** Handles responses from worker threads */
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {

            if(destroyed) return // The service has been destroyed. Do not process the asynchronous message

            val response: String = msg.obj as String
            when (msg.what) {
                HandlerMessageCodes.MESSAGE_ERROR.ordinal -> {
                    errorMessage = response
                    _isError.value = true
                    Log.i(TAG, "ElmError: $errorMessage")

                    // Reset all gauges to 'NO DATA' state:
                    for(parameter in parameterList) {
                        parameter.processResponse(Utilities.NO_DATA)
                    }

                    // Reset scaling factors initialization:
                    ScalingFactors.initialized = false

                    when(msg.arg1) {

                        HandlerMessageCodes.MESSAGE_ERROR_IGNORABLE.ordinal-> if(!stopSending) sendPid()

                        HandlerMessageCodes.MESSAGE_ERROR_BUS_BUSY.ordinal-> {
                            // Send message to service's client
                            clientMessenger?.send(Message.obtain(null, HandlerMessageCodes.MESSAGE_ERROR_BUS_BUSY.ordinal))
                        }

                        HandlerMessageCodes.MESSAGE_ERROR_IGNITION_OFF.ordinal-> {
                            Log.i(TAG, "Ignition OFF detected")
                            if(!stopSending) sendPid()

                        }
                    }
                }


                HandlerMessageCodes.MESSAGE_RESPONSE_SCALING_FACTORS.ordinal -> {
                    Log.i(TAG, "Scaling factors response>$response<")
                    if(ScalingFactors.processScalingFactorsResponse(response, this) && !stopSending) sendPid()
                }

                HandlerMessageCodes.MESSAGE_RESPONSE.ordinal -> {
                    Log.i(TAG, "Response >$response<")
                    Log.i(TAG, "Command for message response: ${ElmHelper.lastCommand}")

                    if(parameterRemoved) { // A parameter has been deleted from the list invalidating 'currentIndex'.
                        currentIndex = 0 // Reset current index to 0.
                        parameterRemoved = false
                    } else {
                        parameterList[currentIndex].processResponse(response)
                        currentIndex++
                    }

                    if (!stopSending)  sendPid()
                }
            }
        }
    }

    override fun onDestroy() {
        destroyed = true
    }

    private companion object {
        private val TAG = "LiveDataService"
    }
}