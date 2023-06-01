package `in`.v89bhp.obdscanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.v89bhp.obdscanner.ui.home.Home
import `in`.v89bhp.obdscanner.ui.home.HomeViewModel
import `in`.v89bhp.obdscanner.ui.home.NavDrawerItem
import `in`.v89bhp.obdscanner.ui.settings.GaugeTypePicker
import `in`.v89bhp.obdscanner.ui.theme.HoloRedLight
import `in`.v89bhp.obdscanner.ui.theme.OBDScannerTheme
import kotlinx.coroutines.launch

/**
 * Screen containing the Navigation host composable with nav drawer component:
 */
@Composable
fun OBDScannerApp(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    appState: OBDScannerAppState = rememberOBDScannerAppState(),
    viewModel: OBDScannerAppViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    ),
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    // Grab the current context in this part of the UI tree
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Box() {
        Column {
            NavHost(
                navController = appState.navController,
                startDestination = Screen.Home.route,
                modifier = modifier.weight(0.97f)
            ) {
                composable(Screen.Home.route) { backStackEntry ->
                    Home(
                        onNavigateTo = { route ->
                            appState.navigateTo(route, backStackEntry)
                        }
                    )
                }
                composable(Screen.GaugeTypePicker.route) { backStackEntry ->
                    GaugeTypePicker(navigateBack = {
                        homeViewModel.selectedItem =
                            NavDrawerItem.SETTINGS // Returning from Gauge type picker option of settings. Show settings screen
                        appState.navigateBack()
                    })
                }

// TODO


                //        composable(Screen.Note.route) { backStackEntry ->
//            Note(fileName = backStackEntry.arguments?.getString("fileName")!!,
//                navigateBack = { appState.navigateBack() })
//        }
            }


            ConnectivityBanner(
                text = "Connected",
                background = HoloRedLight,
                modifier = Modifier.weight(0.03f)
            )

        }

        if (viewModel.showConnectingSnackbar) {
            LaunchedEffect(snackbarHostState) {
                if (snackbarHostState.showSnackbar(
                        message = "Connecting to OBDII...",
                        duration = SnackbarDuration.Indefinite,
                        actionLabel = "Cancel"
                    ) == SnackbarResult.ActionPerformed
                ) {
                    Log.i("OBDScannerApp", "Snackbar action button clicked")
                }

            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }

    LaunchedEffect(true) {
        viewModel.isDestroyed = false
    }

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    context.registerReceiver(
                        viewModel.bluetoothStateChangeReceiver,
                        IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                    )
                    context.registerReceiver(
                        viewModel.bluetoothConnectionStateChangeReceiver, IntentFilter(
                            BluetoothDevice.ACTION_ACL_CONNECTED
                        )
                    )
                    context.registerReceiver(
                        viewModel.bluetoothConnectionStateChangeReceiver, IntentFilter(
                            BluetoothDevice.ACTION_ACL_DISCONNECTED
                        )
                    )
                }

                Lifecycle.Event.ON_PAUSE -> {
                    context.unregisterReceiver(viewModel.bluetoothStateChangeReceiver)
                    context.unregisterReceiver(
                        viewModel.bluetoothConnectionStateChangeReceiver
                    )
                    context.unregisterReceiver(viewModel.bluetoothConnectionStateChangeReceiver)
                }

                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.isDestroyed = true
                }

                else -> {// TODO Add other lifecycle events before this else block.

                }
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            context.unregisterReceiver(viewModel.bluetoothStateChangeReceiver)
            context.unregisterReceiver(
                viewModel.bluetoothConnectionStateChangeReceiver
            )
            context.unregisterReceiver(viewModel.bluetoothConnectionStateChangeReceiver)
        }
    }
}

@Composable
fun ConnectivityBanner(
    text: String,
    background: Color,
    modifier: Modifier = Modifier,
    timeToShowMillis: Long = 0
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(background)
    ) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center),
            color = colorResource(id = android.R.color.white),
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
fun ConnectingSnackbar(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(60.dp)
    ) {
        Text(
            text,
            modifier = Modifier
                .weight(0.7f)
                .padding(start = 8.dp)
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .weight(0.3f)
                .padding(end = 8.dp)
        ) {
            Text("Cancel")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = Devices.NEXUS_7)
@Composable
fun BannerScreen() {
    OBDScannerTheme() {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(id = R.string.app_name))
                    },
                    actions = {

                    },
                    navigationIcon = {
                        IconButton(onClick = {

                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Back"
                            )
                        }
                    })
            },
        ) { contentPadding ->
            Column() {
                Text(
                    "Hello, world!",
                    modifier = Modifier
                        .padding(contentPadding)
                        .weight(0.9f, fill = true)
                )
                if (true) {
                    ConnectingSnackbar(
                        text = "Connecting to OBDII",
                        onClick = {},
                        modifier = Modifier.weight(0.07f)
                    )
                }
                ConnectivityBanner(
                    text = "Connected",
                    background = HoloRedLight,
                    modifier = Modifier.weight(0.03f)
                )
            }


        }
    }
}