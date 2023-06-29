package `in`.v89bhp.obdscanner.ui.connectivity

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.ui.theme.OBDScannerTheme

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Connectivity(
    backStackEntry: NavBackStackEntry,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    connectivityViewModel: ConnectivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = backStackEntry
    )
) {

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.connectivity))
                },
                actions = {

                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })

        }) { contentPadding ->
        Column(modifier = modifier.padding(contentPadding)) {

            val bluetoothMultiplePermissionsState = rememberMultiplePermissionsState(
                listOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                )
            )

            if (bluetoothMultiplePermissionsState.allPermissionsGranted) {
                ConnectionSetupPager()
            } else {

                Card(modifier = Modifier.padding(16.dp)) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.bluetooth_permission_request),


                            )
                        if (bluetoothMultiplePermissionsState.shouldShowRationale ||
                            connectivityViewModel.shouldShowBluetoothPermissionRationale
                        ) {
                            connectivityViewModel.shouldShowBluetoothPermissionRationale = true
                            Text(
                                text = stringResource(R.string.bluetooth_permission_request_rationale),
                                color = colorResource(R.color.red)
                            )
                        }
                        Log.i(
                            "Connectivity",
                            "Should show permission rationale? ${bluetoothMultiplePermissionsState.shouldShowRationale}"
                        )
                        Button(
                            modifier = Modifier.padding(top = 8.dp),
                            onClick = { bluetoothMultiplePermissionsState.launchMultiplePermissionRequest() }) {
                            Text(text = stringResource(R.string.grant_permission))
                        }
                    }
                }
            }

        }
    }


}

@OptIn(ExperimentalPermissionsApi::class)
private fun getTextToShowGivenPermissions(
    permissions: List<PermissionState>,
    shouldShowRationale: Boolean
): String {
    val revokedPermissionsSize = permissions.size
    if (revokedPermissionsSize == 0) return ""

    val textToShow = StringBuilder().apply {
        append("The ")
    }

    for (i in permissions.indices) {
        textToShow.append(permissions[i].permission)
        when {
            revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                textToShow.append(", and ")
            }

            i == revokedPermissionsSize - 1 -> {
                textToShow.append(" ")
            }

            else -> {
                textToShow.append(", ")
            }
        }
    }
    textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
    textToShow.append(
        if (shouldShowRationale) {// Shown when user has denied a previous permission request.
            " important. Please grant all of them for the app to function properly."
        } else {
            " denied. The app cannot function without them."
        }
    )
    return textToShow.toString()
}


@Composable
fun TipCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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