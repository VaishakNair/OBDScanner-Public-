package `in`.v89bhp.obdscanner.ui.connectivity

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import `in`.v89bhp.obdscanner.helpers.BluetoothHelper

class BluetoothConnectionViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        const val TAG = "BluetoothConnectionViewModel"
    }

    var isConnecting by mutableStateOf(false)
    var pairedDevices = mutableListOf<BluetoothDevice>().toMutableStateList()
    var isBtEnabled by mutableStateOf(false)

    val bluetoothAdapter
        get() = BluetoothHelper.bluetoothAdapter


    fun updateBtEnabledStatus() {
        isBtEnabled = bluetoothAdapter?.let {
            it.isEnabled
        } ?: false
    }




}