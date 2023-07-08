package `in`.v89bhp.obdscanner.ui.imreadiness

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry

@Composable
fun IMReadinessSinceDtcCleared(
    backStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
    viewModel: IMReadinessSinceDtcClearedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = backStackEntry
    ),
    lifecycleOwner: LifecycleOwner = backStackEntry
) {


}