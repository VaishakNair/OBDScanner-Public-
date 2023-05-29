package `in`.v89bhp.obdscanner

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.v89bhp.obdscanner.ui.home.Home
import `in`.v89bhp.obdscanner.ui.home.HomeViewModel
import `in`.v89bhp.obdscanner.ui.home.NavDrawerItem
import `in`.v89bhp.obdscanner.ui.settings.GaugeTypePicker
import `in`.v89bhp.obdscanner.ui.theme.HoloRedLight

/**
 * Screen containing the Navigation host composable with nav drawer component:
 */
@Composable
fun OBDScannerApp(
    modifier: Modifier = Modifier,
    appState: OBDScannerAppState = rememberOBDScannerAppState(),
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
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
            modifier = Modifier.weight(0.03f))

    }
}

@Composable
fun ConnectivityBanner(text: String, background: Color, modifier: Modifier = Modifier) {
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