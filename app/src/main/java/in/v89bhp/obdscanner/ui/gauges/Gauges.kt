package `in`.v89bhp.obdscanner.ui.gauges

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
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidViewBinding
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.GaugesFragmentLayoutBinding
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gauges(navigateBack: () -> Unit,
    modifier: Modifier = Modifier) {
// Wrap in scaffold. Implement full screen top bar hiding logic. Implement Snackbar logic
    // using scaffold snackbarhost.



    AndroidViewBinding(
        factory = GaugesFragmentLayoutBinding::inflate
    ) {

    }

    // Gauge settings dialog:
    if (GaugesAppBarState.gaugeSettingsDialogState.show) {
        var isInvalidInput by rememberSaveable { mutableStateOf(false) }
        val parameter =
            ParameterHolder.parameterList[GaugesAppBarState.gaugeSettingsDialogState.parameterIndex]
        AlertDialog(onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        // Save values to corresponding parameter making input textfield red
                        // and showing error message as supporting text in case of a number format exception:

                        parameter.maxAlertValue =
                            GaugesAppBarState.gaugeSettingsDialogState.audioAlertThresholdTextFieldValue.text.toFloat()
                        parameter.audioAlert = GaugesAppBarState.gaugeSettingsDialogState.audioAlert

                        parameter.settingsIcon.visibility = View.GONE // Hide settings icon.

                        // Hide the settings dialog
                        GaugesAppBarState.gaugeSettingsDialogState =
                            GaugeSettingsDialogState(false)
                    } catch (nfe: NumberFormatException) {
                        isInvalidInput = true
                    }
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    parameter.settingsIcon.visibility = View.GONE // Hide settings icon.
                    // Hide the settings dialog
                    GaugesAppBarState.gaugeSettingsDialogState =
                        GaugeSettingsDialogState(false)
                }) { Text(stringResource(R.string.cancel)) }
            },
            title = { Text(GaugesAppBarState.gaugeSettingsDialogState.title) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.audio_alert))
                        Switch(checked = GaugesAppBarState.gaugeSettingsDialogState.audioAlert,
                            onCheckedChange = { isChecked ->
                                GaugesAppBarState.gaugeSettingsDialogState.audioAlert = isChecked
                            })

                    }
                    TextField(
                        value = GaugesAppBarState.gaugeSettingsDialogState.audioAlertThresholdTextFieldValue,
                        onValueChange = {
                            GaugesAppBarState.gaugeSettingsDialogState.audioAlertThresholdTextFieldValue =
                                it
                        },
                        label = { Text(GaugesAppBarState.gaugeSettingsDialogState.label) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = isInvalidInput,
                        supportingText = { Text(if (isInvalidInput) stringResource(R.string.invalid_input) else "") }
                    )
                }
            })
    }
}