package `in`.v89bhp.obdscanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import `in`.v89bhp.obdscanner.ui.connectivity.BluetoothConnectionViewModel
import `in`.v89bhp.obdscanner.ui.theme.OBDScannerTheme

class MainActivity : FragmentActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val EXTRA_DESTINATION_ID = "extra_dest_id"
    }

    private val bluetoothConnectionViewModel by viewModels<BluetoothConnectionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OBDScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OBDScannerApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        applicationContext.registerReceiver(btStateChangedReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun onPause() {
        super.onPause()
        applicationContext?.unregisterReceiver(btStateChangedReceiver)
    }

    private val btStateChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            bluetoothConnectionViewModel.updateBtEnabledStatus()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startBtEnableActivity() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, ActivityRequestCodes.REQUEST_ENABLE_BT.ordinal + 1)
    }


}

enum class ActivityRequestCodes {
    REQUEST_ENABLE_BT, REQUEST_CHECK_LOCATION_SETTINGS
}