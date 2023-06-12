package `in`.v89bhp.obdscanner.ui.scan

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.enums.HandlerMessageCodes
import `in`.v89bhp.obdscanner.helpers.ElmHelper
import `in`.v89bhp.obdscanner.helpers.Utilities.splitMultilineResponse

class ScanTroubleCodesViewModel(application: Application) : AndroidViewModel(application) {
    private var _scanning by mutableStateOf(false)

    private var _clearing by mutableStateOf(false)

    private var _obdCodes = mutableListOf<Pair<String, String>>().toMutableStateList()

    private var _scanCompleted by mutableStateOf(false)

    private var _confirmedCount by mutableStateOf(0)

    private var _pendingCount by mutableStateOf(0)

    private var _permanentCount by mutableStateOf(0)


    val confirmedCount: Int
        get() = _confirmedCount

    val pendingCount:Int
        get() = _pendingCount

    val permanentCount: Int
        get() = _permanentCount

    val obdCodes: List<Pair<String, String>>
        get() = _obdCodes


    val scanCompleted: Boolean
        get() = _scanCompleted

    var errorMessage: String? = null


    val scanning: Boolean
        get() = _scanning

    val clearing: Boolean
    get() = _clearing


    fun startScan() {
        _scanning = true
        _scanCompleted = false
        ElmHelper.send(mHandler, "0101\r")
    }

    fun clearCodes() {
        _clearing = true
        ElmHelper.send(mHandler, "04\r")

    }

    /** Handles responses from worker threads */
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        val obdCodes: MutableList<Pair<String, String>> = mutableListOf()
        var protocolNumber: Int = 0
        override fun handleMessage(msg: Message) {
            var response: String = msg?.obj as String
            when (msg?.what) {
                HandlerMessageCodes.MESSAGE_ERROR.ordinal -> {
                    errorMessage = response
                    _scanning = false
                }

                HandlerMessageCodes.MESSAGE_RESPONSE.ordinal -> {

                    response = response.filter { it != ' ' }// Remove spaces
                    Log.i(APP_NAME, "Response after removing space>$response<")



                    if (response.startsWith("41")) {
                        obdCodes.clear()// Clear any existing codes in the list and reset counters
                        _pendingCount = 0
                        _confirmedCount = 0
                        _permanentCount = 0

                        val lines = splitMultilineResponse(response)
                        Log.i(APP_NAME, "Lines: $lines")
                        var numOfCodes = 0
                        for (line in lines) {
                            numOfCodes = Integer.parseInt(line.substring(4, 6), 16)
                            if (numOfCodes >= 128) {// 'CEL' is ON.
                                numOfCodes -= 128
                            }
                            if (numOfCodes != 0) {
                                break
                            }
                        }

                        ElmHelper.send(this, "AT DPN\r")

                    } else if (response.startsWith("A", true)) {
                        protocolNumber = Integer.parseInt(response.elementAt(1).toString(), 16)
                        ElmHelper.send(this, "03\r")
                    } else if (response.startsWith("43")) {// Confirmed DTCs
                        val lines = splitMultilineResponse(response)
                        for (line in lines) {
                            populateObdCodeList(line, "Confirmed")
                        }
                        _confirmedCount = obdCodes.size
                        ElmHelper.send(this, "07\r")
                    } else if (response.startsWith("47")) {// Pending DTCs
                        val lines = splitMultilineResponse(response)
                        for (line in lines) {
                            populateObdCodeList(line, "Pending")
                        }
                        _pendingCount = obdCodes.size - _confirmedCount as Int
                        ElmHelper.send(this, "0A\r")


                    } else if (response.startsWith("4A")) {// Permanent (cleared) DTCs
                        val lines = splitMultilineResponse(response)
                        for (line in lines) {
                            populateObdCodeList(line, "Permanent")
                        }
                        _permanentCount =
                            obdCodes.size - (_confirmedCount as Int + _pendingCount as Int)

                        ElmHelper.send(this, "020200\r")

                    } else if (response.startsWith("42")) {// DTC that caused freeze frame
                        val lines = splitMultilineResponse(response)
                        for (line in lines) {
                            val decodedObdCode = getDecodedOBDCode(line.substring(6))
                            if (decodedObdCode != "P0000") {// Ignore codes generated by padding 0s
                                obdCodes.find { it.first == decodedObdCode}?.let {
                                    obdCodes[obdCodes.indexOf(it)] = it.first to it.second + " (FF)"
                                }
                            }
                        }

                        showResult()
                    } else if(response.startsWith("44")) {// DTCs have been cleared. Start scan again to update UI.
                        _clearing = false
                        startScan()

                    } else if (response.contains("NODATA") || response.startsWith("7F")) {
                        when (ElmHelper.lastCommand) {
                            "07" -> // No pending DTCs. Check for permanent ones
                            ElmHelper.send(this, "0A\r")
                            "0A" ->// No permanent DTCs. Check for freeze frame causing DTC
                                ElmHelper.send(this, "020200\r")
                            "020200" ->  showResult()
                            "04" -> {// Could not clear DTCs
                                errorMessage = "Could not clear diagnostic trouble code(s)."
                                _clearing = false
                                showResult()
                            }
                            else  -> {
                                errorMessage =
                                    "For command '${ElmHelper.lastCommand}', ECU responded with 'NO DATA'. Please contact developer."
                                showResult()
                            }
                        }
                    }
                }
            }
        }

        fun showResult() {
            if(errorMessage == null) {
//                FirebaseAnalytics.getInstance(application).let {
//                    it.logEvent(Utilities.FirebaseAnalyticsCustomTags.SCAN_COMPLETE, null)
//                }
            }

            _obdCodes.clear()
            _obdCodes.addAll(obdCodes)
            _scanCompleted = true
            _scanning = false
        }



        fun populateObdCodeList(response: String, category: String) {
            val mResponse = if (protocolNumber in 6..9) {
                response.substring(4)
            } else {
                response.substring(2)
            }

            Log.i(APP_NAME, "DTC codes>$mResponse<")

            if (mResponse.isNotEmpty()) {// If no DTCs are present, CAN protocol won't return any.
                for (dtc in mResponse.chunked(4)) {
                    Log.i(APP_NAME, "Chunked dtc>$dtc<")
                    val decodedObdCode = getDecodedOBDCode(dtc)
                    if (decodedObdCode != "P0000") {// Ignore codes generated by padding 0s
                        obdCodes.add(Pair(decodedObdCode, category))
                    }
                }
            }
        }

        fun getDecodedOBDCode(dtc: String): String {
            val prefix = when (dtc[0]) {
                in '0'..'3' -> "P" + dtc.get(0)
                in '4'..'7' -> "C" + (dtc.get(0) - '4').toString()
                '8' -> "B0"
                '9' -> "B1"
                'A' -> "B2"
                'B' -> "B3"
                'C' -> "U0"
                'D' -> "U1"
                'E' -> "U2"
                'F' -> "U3"
                else -> assert(false)
            }
            return "$prefix${dtc.substring(1)}"
        }
    }
}