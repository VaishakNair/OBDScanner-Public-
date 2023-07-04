package `in`.v89bhp.obdscanner.ui.connectivity

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import `in`.v89bhp.obdscanner.enums.HandlerMessageCodes
import `in`.v89bhp.obdscanner.helpers.ElmHelper
import androidx.compose.runtime.*
class ConnectionStatusViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "ConnectionStatusViewModel"
    }

    var isConnecting by mutableStateOf(false)

    var isError by mutableStateOf(false)
    var errorMessage: String = ""

    var elmVersion: String? = null

    private var stopSending: Boolean = false


    fun loadConnectionStatus() {
        isError = false
        isConnecting = true
        loadStatus()
    }

    private fun loadStatus() {
        ElmHelper.send(mHandler, "ATI\r")
    }

    fun cancel() {
        isConnecting = false
        ElmHelper.stop()
    }

    /** Handles responses from worker threads */
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val response: String = msg?.obj as String
            when (msg?.what) {
                HandlerMessageCodes.MESSAGE_ERROR.ordinal -> {
                    if(msg.arg1 != HandlerMessageCodes.MESSAGE_ERROR_IGNITION_OFF.ordinal) {
                        errorMessage = response
                        isError = true
                    }
                    isConnecting = false
                }

                HandlerMessageCodes.MESSAGE_RESPONSE_AT_COMMAND.ordinal -> {
                    elmVersion = response
                    isConnecting = false
                }
            }
        }
    }

    override fun onCleared() {
        stopSending = true
        super.onCleared()
    }

}