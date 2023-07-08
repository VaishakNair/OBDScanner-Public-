package `in`.v89bhp.obdscanner.ui.imreadiness



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.ImReadinessDtcListItemBinding
import `in`.v89bhp.obdscanner.ui.connectivity.CircularProgress
import `in`.v89bhp.obdscanner.ui.connectivity.ErrorCard

@Composable
fun IMReadinessSinceDtcCleared(
    backStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
    viewModel: IMReadinessSinceDtcClearedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = backStackEntry
    ),
    lifecycleOwner: LifecycleOwner = backStackEntry
) {

    if(viewModel.loading) {
        CircularProgress(text = stringResource(R.string.loading))
    } else if(viewModel.isError) {
        ErrorCard(errorMessage = stringResource(R.string.try_again), onClick = { viewModel.loadMonitorStatuses() })
    } else {
       
            LazyColumn {
                items(viewModel.monitorStatuses) {
                    Column() {
                        
                    }
                }
        }
    }

}