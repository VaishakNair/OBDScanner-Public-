package `in`.v89bhp.obdscanner.ui.home


import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.Screen
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder
import `in`.v89bhp.obdscanner.ui.about.About
import `in`.v89bhp.obdscanner.ui.connectivity.Connectivity
import `in`.v89bhp.obdscanner.ui.gauges.Gauges
import `in`.v89bhp.obdscanner.ui.gauges.GaugesAppBarState
import `in`.v89bhp.obdscanner.ui.scan.ScanContainer
import `in`.v89bhp.obdscanner.ui.scan.ScanOther
import `in`.v89bhp.obdscanner.ui.scan.ScanOtherViewModel
import `in`.v89bhp.obdscanner.ui.scan.ScanTroubleCodes
import `in`.v89bhp.obdscanner.ui.settings.Settings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    onNavigateTo: (route: String) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    ),
    scanOtherViewModel: ScanOtherViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        gesturesEnabled = if (GaugesAppBarState.isFullScreen) false else GaugesAppBarState.navDrawerGesturesEnabled,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavDrawerItem.values().forEach { navDrawerItem ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                painterResource(navDrawerItem.icon),
                                contentDescription = null
                            )
                        },
                        label = { Text(navDrawerItem.label) },
                        selected = navDrawerItem == homeViewModel.selectedItem,
                        onClick = {
                            scope.launch { drawerState.close() }
                            performInitializationCleanup(navDrawerItem, scanOtherViewModel)
                            homeViewModel.selectedItem = navDrawerItem

                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                }
            }
        },
        content = {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    if (GaugesAppBarState.isFullScreen.not()) {
                        TopAppBar(
                            title = {
                                Text(homeViewModel.selectedItem.label)
                            },
                            actions = {
                                if (homeViewModel.selectedItem == NavDrawerItem.GAUGES) {
                                    IconButton(onClick = { onNavigateTo(Screen.GaugePicker.route) }) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_add),
                                            contentDescription = "Add Gauge"
                                        )
                                    }
                                    if (ParameterHolder.parameterList.isNotEmpty()) {
                                        IconButton(onClick = {
                                            GaugesAppBarState.onAppBarActionClick(
                                                R.drawable.ic_fullscreen
                                            )
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_fullscreen),
                                                contentDescription = "Fullscreen"
                                            )
                                        }

                                        IconButton(onClick = {
                                            GaugesAppBarState.onAppBarActionClick(
                                                R.drawable.ic_info
                                            )
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_info),
                                                contentDescription = "Info"
                                            )
                                        }
                                        IconButton(onClick = {
                                            GaugesAppBarState.onAppBarActionClick(
                                                R.drawable.ic_toggle_hud
                                            )
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_toggle_hud),
                                                contentDescription = "Toggle HUD"
                                            )
                                        }
                                    }
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    // TODO
                                    with(drawerState) {
                                        if (isOpen) scope.launch { close() }
                                        else scope.launch { open() }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Back"
                                    )
                                }
                            })
                    } else {
                    }
                }) { contentPadding ->
                when (homeViewModel.selectedItem) {
                    NavDrawerItem.GAUGES -> Gauges(
                        modifier = Modifier.padding(
                            contentPadding
                        )
                    )

                    NavDrawerItem.SCAN -> ScanContainer(
                        modifier = Modifier.padding(
                            contentPadding
                        )
                    )

                    NavDrawerItem.CONNECTIVITY -> Connectivity(
                        modifier = Modifier.padding(
                            contentPadding
                        )
                    )

                    NavDrawerItem.SETTINGS -> Settings(
                        onNavigateTo = onNavigateTo,
                        modifier = Modifier.padding(contentPadding)
                    )
                    NavDrawerItem.ABOUT -> About(modifier = Modifier.padding(contentPadding))

                // TODO Add more screens
                }
            }
            if (GaugesAppBarState.showExitFullScreenSnackbar) {
                val fullScreenHint = stringResource(R.string.fullscreenHint)
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = fullScreenHint
                    )
                    GaugesAppBarState.showExitFullScreenSnackbar = false
                }

            }
            if (GaugesAppBarState.showTryAgainSnackbar) {
                val tryAgainLabel = stringResource(R.string.try_again)
                val message = stringResource(R.string.bus_busy)
                LaunchedEffect(snackbarHostState) {
                    if (snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Long,
                            actionLabel = tryAgainLabel
                        ) == SnackbarResult.ActionPerformed
                    ) {
                        GaugesAppBarState.tryAgain()
                    }
                    GaugesAppBarState.showTryAgainSnackbar = false
                }
            }
        }

    )

    if (drawerState.isOpen || homeViewModel.selectedItem != HomeViewModel.HOME_ITEM) {
        // IMPORTANT: Enable BackHandler() only when needed. Otherwise it affects
        // how the UI gets composed.
        BackHandler() {
            if (drawerState.isOpen) {
                scope.launch { drawerState.close() }
            } else if (homeViewModel.selectedItem != HomeViewModel.HOME_ITEM) {
                homeViewModel.selectedItem = HomeViewModel.HOME_ITEM
            }
        }
    }

}

enum class NavDrawerItem(@DrawableRes val icon: Int, val label: String) {
    GAUGES(R.drawable.ic_gauges, "Gauges"),
    SCAN(R.drawable.ic_scan, "Scan"),
    CONNECTIVITY(R.drawable.baseline_bluetooth_connected_24, "Connectivity"),
    SETTINGS(R.drawable.baseline_settings_24, "Settings"),
    ABOUT(R.drawable.ic_info_outline, "About")
}

fun performInitializationCleanup(navDrawerItem: NavDrawerItem, scanOtherViewModel: ScanOtherViewModel) {
    if(navDrawerItem == NavDrawerItem.SCAN) {
        scanOtherViewModel.stopSending = false
    } else {
        scanOtherViewModel.onCleared()
    }
}