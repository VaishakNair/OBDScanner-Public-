package `in`.v89bhp.obdscanner

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.v89bhp.obdscanner.ui.home.Home

/**
 * Screen containing the Navigation host composable with nav drawer component:
 */
@Composable
fun OBDScannerApp(appState: OBDScannerAppState = rememberOBDScannerAppState()) {

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
// TODO


    //        composable(Screen.Note.route) { backStackEntry ->
//            Note(fileName = backStackEntry.arguments?.getString("fileName")!!,
//                navigateBack = { appState.navigateBack() })
//        }
    }

}