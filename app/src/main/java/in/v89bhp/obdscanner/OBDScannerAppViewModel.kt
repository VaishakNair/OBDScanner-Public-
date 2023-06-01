package `in`.v89bhp.obdscanner

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.helpers.BluetoothHelper
import `in`.v89bhp.obdscanner.ui.theme.ConnectivityGreen
import `in`.v89bhp.obdscanner.ui.theme.ConnectivityYellow

class OBDScannerAppViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "OBDScannerAppViewModel"
    }

    var stopTrying = false
    var isDestroyed = false

    var connectivityBannerState by mutableStateOf(
        ConnectivityBannerState(
            show = true,
            message = application.getString(R.string.offline),
            background = ConnectivityYellow
        )
    )
    var showConnectingSnackbar by mutableStateOf(false)

    lateinit var lastConnectedDevice: BluetoothDevice

    val bluetoothStateChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bluetoothState = intent?.getIntExtra(
                BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.STATE_OFF
            )
            if (bluetoothState == BluetoothAdapter.STATE_OFF) {
                // Display 'You are offline' header
                connectivityBannerState = ConnectivityBannerState(
                    show = true, message = application.getString(
                        R.string.offline
                    ), background = ConnectivityYellow
                )


            } else if (bluetoothState == BluetoothAdapter.STATE_ON) {
                establishLastConnection()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun establishLastConnection() {
        if (!BluetoothHelper.connecting) {
            val (isLastConnectedDeviceAvailable, device) = isLastConnectedDeviceAvailable()
            if (isLastConnectedDeviceAvailable) {
                // Try connecting to last connected device. Don't ask for PIN.
                lastConnectedDevice = device as BluetoothDevice

                connectivityBannerState = ConnectivityBannerState(
                    show = true,
                    message = (getApplication() as Context).getString(
                        R.string.connecting_to,
                        device!!.name
                    ),
                    background = ConnectivityYellow
                )

                showConnectingSnackbar = true
                BluetoothHelper.connect(bluetoothConnectionHandler, device)
            } else {
                // Display 'You are offline' header
                connectivityBannerState = ConnectivityBannerState(
                    show = true,
                    message = (getApplication() as Context).getString(R.string.offline),
                    background = ConnectivityYellow
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun isLastConnectedDeviceAvailable(): Pair<Boolean, BluetoothDevice?> {
        val deviceName = PreferenceManager.getDefaultSharedPreferences(getApplication())
            .getString("deviceName", null)
        return deviceName?.let {
            BluetoothHelper.queryPairedDevices()?.let {
                val bluetoothDevice = it.find {
                    it.name == deviceName
                }
                bluetoothDevice?.let {
                    (true to it)
                } ?: (false to null)
            } ?: (false to null)
        } ?: (false to null)
    }

    /** Handles bluetooth connection establishment responses */
    private val bluetoothConnectionHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {

        @SuppressLint("MissingPermission")
        override fun handleMessage(msg: Message) {
            if (!isDestroyed) {
                when (msg?.arg1) {
                    0 -> {// Connection failed. Retry till user cancellation:
                        if (!stopTrying) {
                            Log.i(TAG, "Retrying connection...")
                            BluetoothHelper.connect(this, lastConnectedDevice)
                        } else {
                            Log.i(TAG, "Stopping trying...")
                            stopTrying = false
                        }
                    }

                    1 -> {// Connection established
                        // Dismiss the 'Connecting to' snackbar
                        showConnectingSnackbar = false
                        // Display green 'Connected to $pairedDeviceName' header
                        connectivityBannerState = ConnectivityBannerState(
                            show = true,
                            message = (getApplication() as Context).getString(
                                R.string.connected_to,
                                lastConnectedDevice!!.name
                            ),
                            background = ConnectivityGreen
                        )  // TODO Wire logic to
                        // hide connectivity banner after 5 secs
                        //                        showConnectivityHeader(getString(R.string.connected_to, lastConnectedDevice!!.name), R.color.connectivity_green, 5000)
                    }
                }
            }
//            isDestroyed = false
        }
    }

    val bluetoothConnectionStateChangeReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val bluetoothConnectionState = intent?.action
            val bluetoothDevice =
                intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

            when (bluetoothConnectionState) {
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    // Bluetooth connection lost. Try connecting to the last connected device.
                    establishLastConnection()
                }

                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    // Display green 'Connected to $pairedDeviceName' header
                    connectivityBannerState = ConnectivityBannerState(
                        show = true,
                        message = (getApplication() as Context).getString(
                            R.string.connected_to,
                            bluetoothDevice!!.name
                        ),
                        background = ConnectivityGreen
                    ) // TODO Wire logic to
                    // hide connectivity banner after 5 secs


                    // Store name of connected device to default shared preferences file
                    PreferenceManager.getDefaultSharedPreferences(getApplication()).edit()
                        .putString("deviceName", bluetoothDevice.name).apply()
                }
            }
        }
    }
}

data class ConnectivityBannerState(val show: Boolean, val message: String, val background: Color)