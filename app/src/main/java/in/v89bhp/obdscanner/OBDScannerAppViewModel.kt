package `in`.v89bhp.obdscanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.helpers.BluetoothHelper
import `in`.v89bhp.obdscanner.room.AppRoomDatabase
import `in`.v89bhp.obdscanner.ui.theme.ConnectivityGreen
import `in`.v89bhp.obdscanner.ui.theme.ConnectivityYellow
import `in`.v89bhp.obdscanner.ui.theme.HoloGreenLight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OBDScannerAppViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "OBDScannerAppViewModel"
    }

    var shouldShowBluetoothPermissionDeniedButton by mutableStateOf(false)

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


    private fun establishLastConnection() {
        if (ActivityCompat.checkSelfPermission(
                (getApplication()),
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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
    }


    private fun isLastConnectedDeviceAvailable(): Pair<Boolean, BluetoothDevice?> {
        return if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val deviceName = PreferenceManager.getDefaultSharedPreferences(getApplication())
                .getString("deviceName", null)
            deviceName?.let {
                BluetoothHelper.queryPairedDevices()?.let {
                    val bluetoothDevice = it.find {
                        it.name == deviceName
                    }
                    bluetoothDevice?.let {
                        (true to it)
                    } ?: (false to null)
                } ?: (false to null)
            } ?: (false to null)
        } else {
            (false to null)
        }
    }

    /** Handles bluetooth connection establishment responses */
    private val bluetoothConnectionHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {

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
                                try {
                                    lastConnectedDevice!!.name
                                } catch (ex: SecurityException) {
                                    Log.e(TAG, "Bluetooth permission(s) not granted", ex)
                                    ""
                                }
                            ),
                            background = ConnectivityGreen,
                            autoHide = true // Hides connectivity banner after 5 secs
                        )
                    }
                }
            }
//            isDestroyed = false
        }
    }

    val bluetoothConnectionStateChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bluetoothConnectionState = intent?.action
            val bluetoothDevice =
                intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

            when (bluetoothConnectionState) {
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    // Bluetooth connection lost. Try connecting to the last connected device.
                    establishLastConnection()
                }

                BluetoothDevice.ACTION_ACL_CONNECTED -> { // Broadcast when phone is connected to a device.
                    // Our bluetooth socket may still be unconnected to the device.
                    if (BluetoothHelper.socket?.isConnected != true) {
                        establishLastConnection()
                    }
                }

                BluetoothHelper.ACTION_BT_CONNECTED -> {
                    try {
                        // Display green 'Connected to $pairedDeviceName' header
                        connectivityBannerState = ConnectivityBannerState(
                            show = true,
                            message = (getApplication() as Context).getString(
                                R.string.connected_to,
                                bluetoothDevice!!.name
                            ),
                            background = ConnectivityGreen,
                            autoHide = true  // Hides connectivity banner after 5 secs
                        )

                        // Store name of connected device to default shared preferences file
                        PreferenceManager.getDefaultSharedPreferences(getApplication()).edit()
                            .putString("deviceName", bluetoothDevice.name).apply()
                    } catch (ex: SecurityException) {
                        Log.e(TAG, "Bluetooth permission(s) not granted", ex)
                        connectivityBannerState = ConnectivityBannerState(
                            show = true,
                            message = (getApplication() as Context).getString(R.string.offline),
                            background = ConnectivityYellow
                        )
                    }
                }
            }
        }
    }

    fun hideConnectivityBanner() {
        connectivityBannerState = ConnectivityBannerState(false, "", HoloGreenLight)
    }

    fun updateConnectivityBanner() {
        BluetoothHelper.bluetoothAdapter?.let {
            if (!it.isEnabled) {
                connectivityBannerState = ConnectivityBannerState(
                    true,
                    (getApplication() as Context).getString(R.string.offline),
                    ConnectivityYellow
                )

            } else {
                if (BluetoothHelper.socket?.isConnected?.not() != false) {
                    establishLastConnection()
                }
            }
        }
    }

    fun cancelConnection() {
        stopTrying = true
        BluetoothHelper.close() // Sometimes the socket connection might hang. 'close()' will force-quit it.
        connectivityBannerState = ConnectivityBannerState(
            true,
            (getApplication() as Context).getString(R.string.offline),
            ConnectivityYellow
        )
    }

    fun loadDatabase() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                AppRoomDatabase.getDatabase(getApplication())
            }
        }
    }

    fun updateBluetoothPermissionGrantedState() {
        // Set shouldShowBluetoothPermissionDenied button to true if
        // 1. shared prefs rationale is true
        // 2. Bluetooth permissions are not granted.
        // 3. We are currently not in the 'Connectivity' nav destination.

    }
}

data class ConnectivityBannerState(
    val show: Boolean,
    val message: String,
    val background: Color,
    val autoHide: Boolean = false
)