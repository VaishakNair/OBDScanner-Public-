package `in`.v89bhp.obdscanner

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.helpers.BluetoothHelper
import `in`.v89bhp.obdscanner.ui.theme.ConnectivityYellow

class OBDScannerAppViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "OBDScannerAppViewModel"
    }

    var showConnectivityHeader by mutableStateOf(false)
    var connectivityHeaderMessage by mutableStateOf(application.getString(R.string.offline))
    var connectivityHeaderBackground by mutableStateOf(ConnectivityYellow)
    lateinit var lastConnectedDevice: BluetoothDevice

    val bluetoothStateChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bluetoothState = intent?.getIntExtra(
                BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.STATE_OFF
            )
            if (bluetoothState == BluetoothAdapter.STATE_OFF) {
                // Display 'You are offline' header
                showConnectivityHeader = true
                connectivityHeaderMessage = application.getString(R.string.offline)
                connectivityHeaderBackground = ConnectivityYellow

            } else if (bluetoothState == BluetoothAdapter.STATE_ON) {
                establishLastConnection()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun establishLastConnection() {
        if(!BluetoothHelper.connecting) {
            val (isLastConnectedDeviceAvailable, device) = isLastConnectedDeviceAvailable()
            if(isLastConnectedDeviceAvailable) {
                // Try connecting to last connected device. Don't ask for PIN.
                lastConnectedDevice = device as BluetoothDevice
                showConnectivityHeader = true
                connectivityHeaderMessage = (getApplication() as Context).getString(R.string.connecting_to, device!!.name)
                connectivityHeaderBackground = ConnectivityYellow
                snackbar.show()
                BluetoothHelper.connect(bluetoothConnectionHandler, device)
            } else {
                // Display 'You are offline' header
                showConnectivityHeader(getString(R.string.offline), R.color.connectivity_yellow)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun isLastConnectedDeviceAvailable(): Pair<Boolean, BluetoothDevice?> {
        val deviceName = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("deviceName", null)
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
}