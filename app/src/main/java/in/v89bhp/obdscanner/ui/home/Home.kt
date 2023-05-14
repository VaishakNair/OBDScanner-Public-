package `in`.v89bhp.obdscanner.ui.home


import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.ui.connectivity.Connectivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    onNavigateTo: (destination: String) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    ),
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf(NavDrawerItem.CONNECTIVITY) }

    ModalNavigationDrawer(
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
                        selected = navDrawerItem == selectedItem,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem = navDrawerItem
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                }
            }
        },
        content = {

//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(text = selectedItem.label)
//                Spacer(Modifier.height(20.dp))
//                Button(onClick = { scope.launch { drawerState.open() } }) {
//                    Text("Click to open")
//                }
//            }
            when (selectedItem) {
                NavDrawerItem.CONNECTIVITY -> Connectivity()
//                NavDrawerItem.ABOUT -> About() // TODO
                else -> throw AssertionError("Home navigation drawer selection. Shouldn't reach here")
            }
        }
    )


}

enum class NavDrawerItem(@DrawableRes val icon: Int, val label: String) {
    CONNECTIVITY(R.drawable.baseline_bluetooth_connected_24, "Connectivity")
}