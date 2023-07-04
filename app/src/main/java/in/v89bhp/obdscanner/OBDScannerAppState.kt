package `in`.v89bhp.obdscanner

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


/**
 * List of screens for [OBDScannerApp]
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")

    object GaugeTypePicker : Screen("gaugetypepicker")

    object GaugePicker : Screen("gaugepicker")

    object FreezeFrame : Screen("freezeframe/{obdCode}") {
        fun createRoute(obdCode: String) = "freezeframe/$obdCode"
    }
}


@Composable
fun rememberOBDScannerAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    OBDScannerAppState(navController, context)
}

class OBDScannerAppState(
    val navController: NavHostController,
    private val context: Context
) {

    fun navigateTo(route: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(route)
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }


}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED