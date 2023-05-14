package `in`.v89bhp.obdscanner.ui.connectivity

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.ui.theme.OBDScannerTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Connectivity(
    modifier: Modifier = Modifier,
    connectivityViewModel: ConnectivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    Column {
        // Camera permission state
        val bluetoothConnectPermissionState = rememberPermissionState(
            android.Manifest.permission.BLUETOOTH_CONNECT
        )

        if (bluetoothConnectPermissionState.status.isGranted) {
            ConnectivityCard()
            TipCard()
        } else {
            Column {
                val textToShow = stringResource(id = if (bluetoothConnectPermissionState.status.shouldShowRationale) {
                    // If the user has denied the permission but the rationale can be shown,
                    // then gently explain why the app requires this permission
                    R.string.bluetooth_permission_request_rationale
                } else {
                    // If it's the first time the user lands on this feature, or the user
                    // doesn't want to be asked again for this permission, explain that the
                    // permission is required
                    R.string.bluetooth_permission_request
                })
                Text(textToShow)
                Button(onClick = { bluetoothConnectPermissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
        }
    }
}

@Composable
fun ConnectivityCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(id = R.string.bluetooth_connection_status).format("Connected"))
            Text(text = stringResource(id = R.string.obd_connection_status).format("Connected"))
        }
    }
}

@Composable
fun TipCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(id = R.string.connectivity_tips))

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectivityCardPreview() {
    OBDScannerTheme() {

        TipCard()
    }
}