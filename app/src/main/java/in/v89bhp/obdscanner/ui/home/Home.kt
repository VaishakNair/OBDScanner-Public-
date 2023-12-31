package `in`.v89bhp.obdscanner.ui.home


import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.ElmHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    onNavigateTo: (route: String) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                actions = {

                }
            )

        }) { contentPadding ->

        NavDestinationsGrid(
            modifier = Modifier.padding(contentPadding),
            navigationDestinations = NavigationDestination.values().toList(),
            onNavigateTo = onNavigateTo
        )
    }


}

enum class NavigationDestination(@DrawableRes val icon: Int, val label: String, val route: String) {
    GAUGES(R.drawable.ic_gauges, "Gauges", "gauges"),
    SCAN(R.drawable.ic_scan, "Scan", "scan"),
    CONNECTIVITY(R.drawable.baseline_bluetooth_connected_24, "Connectivity", "connectivity"),
    IM_READINESS(R.drawable.ic_assignment_black_24dp, "I/M Readiness", "imreadiness"),
    SETTINGS(R.drawable.baseline_settings_24, "Settings", "settings"),
    ABOUT(R.drawable.ic_info_outline, "About", "about")
}


@Composable
fun NavDestinationsGrid(
    navigationDestinations: List<NavigationDestination>,
    onNavigateTo: (route: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier.padding(16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(navigationDestinations) { navigationDestination ->
            NavDestinationCard(
                navigationDestination = navigationDestination,
                onClick = { onNavigateTo(navigationDestination.route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavDestinationCard(
    navigationDestination: NavigationDestination,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .size(width = 110.dp, height = 110.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
    {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(navigationDestination.icon),
                contentDescription = null
            )

            Text(text = navigationDestination.label)
            if (navigationDestination == NavigationDestination.CONNECTIVITY) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Text(modifier = Modifier.padding(end = 8.dp),
                        text = stringResource(R.string.obd),
                        style = MaterialTheme.typography.labelLarge,
                        color = colorResource(
                            id = if (ElmHelper.elmInitialized.observeAsState(
                                    initial = false
                                ).value
                            ) R.color.green else R.color.red
                        )
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.ecu),
                        style = MaterialTheme.typography.labelLarge,
                        color = colorResource(
                            id = if (ElmHelper.ecuInitialized.observeAsState(
                                    initial = false
                                ).value
                            ) R.color.green else R.color.red
                        )
                    )
                }
            }
        }
    }

}