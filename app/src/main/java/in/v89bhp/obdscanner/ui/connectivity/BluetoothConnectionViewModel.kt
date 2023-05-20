package `in`.v89bhp.obdscanner.ui.connectivity

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel

class BluetoothConnectionViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        const val TAG = "BluetoothConnectionViewModel"
    }

    var isConnecting by mutableStateOf(false)
    var pairedDevices = mutableListOf<BluetoothDevice>().toMutableStateList()



}