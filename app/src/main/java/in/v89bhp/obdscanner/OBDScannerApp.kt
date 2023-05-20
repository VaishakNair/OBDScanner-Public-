package `in`.v89bhp.obdscanner

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.v89bhp.obdscanner.ui.home.Home
import `in`.v89bhp.obdscanner.ui.home.HomeViewModel
import `in`.v89bhp.obdscanner.ui.home.NavDrawerItem
import `in`.v89bhp.obdscanner.ui.settings.GaugeTypePicker

/**
 * Screen containing the Navigation host composable with nav drawer component:
 */
@Composable
fun OBDScannerApp(appState: OBDScannerAppState = rememberOBDScannerAppState(),
homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
    viewModelStoreOwner = LocalContext.current as ComponentActivity
)) {

    NavHost(
        navController = appState.navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) { backStackEntry ->
            Home(
                onNavigateTo = { route ->
                    appState.navigateTo(route, backStackEntry)
                }
            )
        }
        composable(Screen.GaugeTypePicker.route) { backStackEntry ->
            GaugeTypePicker( navigateBack = {
                homeViewModel.selectedItem = NavDrawerItem.SETTINGS // Returning from Gauge type picker option of settings. Show settings screen
                appState.navigateBack()
            })
        }

// TODO


        //        composable(Screen.Note.route) { backStackEntry ->
//            Note(fileName = backStackEntry.arguments?.getString("fileName")!!,
//                navigateBack = { appState.navigateBack() })
//        }
    }

}