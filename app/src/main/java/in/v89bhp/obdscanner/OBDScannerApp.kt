package `in`.v89bhp.obdscanner

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.v89bhp.obdscanner.ui.home.Home
import `in`.v89bhp.obdscanner.ui.settings.GaugeTypePicker

/**
 * Screen containing the Navigation host composable with nav drawer component:
 */
@Composable
fun OBDScannerApp(onFinish: () -> Unit, appState: OBDScannerAppState = rememberOBDScannerAppState()) {

    NavHost(
        navController = appState.navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) { backStackEntry ->
            Home(
                onNavigateTo = { route ->
                    appState.navigateTo(route, backStackEntry)
                },
                onFinish = onFinish
            )
        }
        composable(Screen.GaugeTypePicker.route) { backStackEntry ->
            GaugeTypePicker()
        }

// TODO


        //        composable(Screen.Note.route) { backStackEntry ->
//            Note(fileName = backStackEntry.arguments?.getString("fileName")!!,
//                navigateBack = { appState.navigateBack() })
//        }
    }

}