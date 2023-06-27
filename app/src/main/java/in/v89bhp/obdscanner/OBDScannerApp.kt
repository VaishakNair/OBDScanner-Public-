package `in`.v89bhp.obdscanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.v89bhp.obdscanner.helpers.BluetoothHelper
import `in`.v89bhp.obdscanner.ui.gauges.GaugePicker
import `in`.v89bhp.obdscanner.ui.gauges.Gauges
import `in`.v89bhp.obdscanner.ui.home.Home
import `in`.v89bhp.obdscanner.ui.home.HomeViewModel
import `in`.v89bhp.obdscanner.ui.home.NavigationDestination
import `in`.v89bhp.obdscanner.ui.scan.FreezeFrame
import `in`.v89bhp.obdscanner.ui.scan.ScanContainer
import `in`.v89bhp.obdscanner.ui.settings.GaugeTypePicker
import kotlinx.coroutines.delay
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
    val scope = rememberCoroutineScope()

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

                composable("gauges") { backStackEntry ->
                    Gauges(
                        onNavigateTo = { route -> appState.navigateTo(route, backStackEntry) },
                        navigateBack = { appState.navigateBack() }
                    )
                }

                composable("scan") { backStackEntry ->
                    ScanContainer(
                        backStackEntry = backStackEntry,
                        onNavigateTo = { route -> appState.navigateTo(route, backStackEntry) },
                        navigateBack = { appState.navigateBack() }
                    )
                }

                // TODO

                composable(Screen.GaugeTypePicker.route) { backStackEntry ->
                    GaugeTypePicker(navigateBack = {
                        homeViewModel.selectedItem =
                            NavigationDestination.SETTINGS // Returning from Gauge type picker option of settings. Show settings screen
                        appState.navigateBack()
                    })
                }
                composable(Screen.GaugePicker.route) { backStackEntry ->
                    GaugePicker(navigateBack = {
                        homeViewModel.selectedItem =
                            NavigationDestination.GAUGES // Returning from Gauge picker option of Gauges. Show Gauges
                        appState.navigateBack()
                    })
                }

                composable(Screen.FreezeFrame.route) { backStackEntry ->
                    FreezeFrame(
                        obdCode = backStackEntry.arguments!!.getString("obdCode")!!,
                        navigateBack = {
                            homeViewModel.selectedItem =
                                NavigationDestination.SCAN // Returning from freeze frame option of Scan. Show scan screen
                            appState.navigateBack()
                        })
                }

// TODO


                //        composable(Screen.Note.route) { backStackEntry ->
//            Note(fileName = backStackEntry.arguments?.getString("fileName")!!,
//                navigateBack = { appState.navigateBack() })
//        }
            }

            with(viewModel.connectivityBannerState) {
                if (show) {
                    ConnectivityBanner(
                        text = message,
                        background = background,
                        autoHide = autoHide,
                        onHide = { viewModel.hideConnectivityBanner() },
                        modifier = Modifier.weight(0.03f)

                    )

                }
            }

        }

        if (viewModel.showConnectingSnackbar) {
            val cancelLabel = stringResource(R.string.cancel)
            LaunchedEffect(snackbarHostState) {
                if (snackbarHostState.showSnackbar(
                        message = viewModel.connectivityBannerState.message,
                        duration = SnackbarDuration.Indefinite,
                        actionLabel = cancelLabel
                    ) == SnackbarResult.ActionPerformed
                ) {
                    viewModel.cancelConnection()
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

    LaunchedEffect(true) {// Load Database
        viewModel.loadDatabase()
    }

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events

        val observer = LifecycleEventObserver { _, event ->
            when (event) {

                Lifecycle.Event.ON_START -> {
                    viewModel.updateConnectivityBanner()
                }

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
                            BluetoothHelper.ACTION_BT_CONNECTED
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
        }
    }
}

@Composable
fun ConnectivityBanner(
    text: String,
    background: Color,
    autoHide: Boolean,
    onHide: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val currentOnHide by rememberUpdatedState(onHide)
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
    if (autoHide) {
        LaunchedEffect(true) {
            launch {
                delay(5000)
                currentOnHide()
            }
        }
    }
}