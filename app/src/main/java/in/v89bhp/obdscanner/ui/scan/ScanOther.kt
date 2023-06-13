package `in`.v89bhp.obdscanner.ui.scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LifecycleOwner
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.ui.connectivity.CircularProgress
import `in`.v89bhp.obdscanner.ui.connectivity.ErrorCard

@Composable
fun ScanOther(
    modifier: Modifier = Modifier,
    viewModel: ScanOtherViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (viewModel.fetching) {
            CircularProgress(text = stringResource(R.string.fetching))
        } else if (viewModel.isError) {
            ErrorCard(
                errorMessage = viewModel.errorMessage!!,
                onClick = { viewModel.loadOtherData() })
        } else if (viewModel.scanCompleted) {
            ScanOtherCompleted(viewModel)
        } else {
            StartScan(onClick = { viewModel.loadOtherData() })
        }
    }
}

@Composable
fun ScanOtherCompleted(
    viewModel: ScanOtherViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

    }
}