package `in`.v89bhp.obdscanner.ui.connectivity

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.provider.Settings
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

    @SuppressLint("MissingPermission")
    fun turnBluetoothOn(context: Context) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        context.startActivity(enableBtIntent)
    }

    fun queryPairedDevices() {
        pairedDevices.clear()
        BluetoothHelper.queryPairedDevices()?.let { pairedDevices.addAll(it) }
    }

    fun showBluetoothSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }


}