package `in`.v89bhp.obdscanner.ui.scan


import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.ScanOtherFragmentLayoutBinding

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScanTroubleCodes(modifier: Modifier = Modifier,
viewModel: ScanTroubleCodesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
    viewModelStoreOwner = LocalContext.current as ComponentActivity
)) {

    // TODO


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