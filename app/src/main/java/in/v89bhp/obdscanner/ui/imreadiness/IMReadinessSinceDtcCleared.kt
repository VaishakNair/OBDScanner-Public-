package `in`.v89bhp.obdscanner.ui.imreadiness


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.ui.connectivity.CircularProgress
import `in`.v89bhp.obdscanner.ui.connectivity.ErrorCard
import `in`.v89bhp.obdscanner.ui.theme.ConnectivityGreen
import `in`.v89bhp.obdscanner.ui.theme.HoloRedLight

@Composable
fun IMReadinessSinceDtcCleared(
    backStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
    viewModel: IMReadinessSinceDtcClearedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = backStackEntry
    ),
    lifecycleOwner: LifecycleOwner = backStackEntry
) {

    LaunchedEffect(viewModel) {
        viewModel.loadMonitorStatuses()
    }

    if (viewModel.loading) {
        CircularProgress(text = stringResource(R.string.loading))
    } else if (viewModel.isError) {
        ErrorCard(
            errorMessage = viewModel.errorMessage!!,
            onClick = { viewModel.loadMonitorStatuses() })
    } else {

        LazyColumn {
            items(viewModel.monitorStatuses) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = it.monitorName)
                    Text(
                        text = it.status,
                        color = if (it.status == stringResource(id = R.string.complete)) ConnectivityGreen else HoloRedLight
                    )
                }
            }
        }
    }

}