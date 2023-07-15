package `in`.v89bhp.obdscanner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import `in`.v89bhp.obdscanner.helpers.BluetoothHelper
import `in`.v89bhp.obdscanner.helpers.ElmHelper
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

        ElmHelper.applicationContext = applicationContext
        BluetoothHelper.applicationContext = applicationContext


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
}
