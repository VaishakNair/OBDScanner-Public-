package `in`.v89bhp.obdscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import `in`.v89bhp.obdscanner.ui.theme.OBDScannerTheme
import kotlin.system.exitProcess

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OBDScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OBDScannerApp(onFinish = {
                        finishAffinity()
                        exitProcess(0) // If process is not exited, the home screen composable doesn't gets drawn when the app is opened again
                                            // say from the 'Recents' screen.
                    })
                }
            }
        }
    }

    companion object {
        const val EXTRA_DESTINATION_ID = "extra_dest_id"
    }
}
