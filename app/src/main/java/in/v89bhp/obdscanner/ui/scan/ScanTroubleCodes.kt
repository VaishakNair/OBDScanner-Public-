package `in`.v89bhp.obdscanner.ui.scan


import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.ui.connectivity.CircularProgress

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScanTroubleCodes(
    modifier: Modifier = Modifier,
    viewModel: ScanTroubleCodesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {

    // TODO
    Box(modifier = modifier) {
        if (viewModel.scanning) {
            CircularProgress(text = stringResource(R.string.scanning))
        } else if (viewModel.clearing) {
            CircularProgress(text = stringResource(R.string.clearing))
        } else if (viewModel.scanCompleted) {
            ScanCompleted(viewModel)
        } else {
            StartScan(onClick = { viewModel.startScan() })
        }

        // Clear trouble codes dialog:
        if (ScanUiState.showClearTroubleCodesDialog) {
            AlertDialog(onDismissRequest = { },
                confirmButton = {
                    TextButton(onClick = {
                        ScanUiState.clearTroubleCodes()
                    }) {
                        Text(stringResource(R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        ScanUiState.showClearTroubleCodesDialog = false
                    }) { Text(stringResource(R.string.cancel)) }
                },
                title = {},
                text = {
                    Text(stringResource(R.string.clear_dtc_confirmation_message))
                })
        }
    }
}

@Composable
fun StartScan(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.start_scan_hint),
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.padding(8.dp)
        )
        Button(onClick = onClick) {
            Text(text = stringResource(R.string.start_scan))
        }
    }
}

@Composable
fun ScanCompleted(viewModel: ScanTroubleCodesViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ScanResultCard()
        LazyColumn() {
            items(viewModel.obdCodes, key= {it}) {

            }
        }
    }
}

@Composable
fun ScanResultCard(modifier: Modifier = Modifier) {
// TODO
}