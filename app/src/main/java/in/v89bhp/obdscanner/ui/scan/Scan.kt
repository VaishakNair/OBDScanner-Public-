package `in`.v89bhp.obdscanner.ui.scan

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidViewBinding
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.ScanFragmentLayoutBinding
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Scan(modifier: Modifier = Modifier) {
    AndroidViewBinding(
        modifier = modifier,
        factory = ScanFragmentLayoutBinding::inflate
    ) {

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