package `in`.v89bhp.obdscanner.ui.gauges

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.Window
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.Screen
import `in`.v89bhp.obdscanner.databinding.GaugesFragmentLayoutBinding
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gauges(
    onNavigateTo: (route: String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val systemUiController: SystemUiController = rememberSystemUiController()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (GaugesAppBarState.isFullScreen.not()) {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.gauges))
                    },
                    actions = {
                        IconButton(onClick = { onNavigateTo(Screen.GaugePicker.route) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = "Add Gauge"
                            )
                        }
                        if (ParameterHolder.parameterList.isNotEmpty()) {
                            IconButton(onClick = {
                                GaugesAppBarState.onAppBarActionClick(
                                    R.drawable.ic_fullscreen
                                )
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_fullscreen),
                                    contentDescription = "Fullscreen"
                                )
                            }

                            IconButton(onClick = {
                                GaugesAppBarState.onAppBarActionClick(
                                    R.drawable.ic_info
                                )
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_info),
                                    contentDescription = "Info"
                                )
                            }
                            IconButton(onClick = {
                                GaugesAppBarState.onAppBarActionClick(
                                    R.drawable.ic_toggle_hud
                                )
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_toggle_hud),
                                    contentDescription = "Toggle HUD"
                                )
                            }
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    })
            } else { // Fullscreen mode. No Top app bar
            }
        }) { contentPadding ->

        AndroidViewBinding(
            modifier = if (GaugesAppBarState.isFullScreen.not()) Modifier.padding(contentPadding) else Modifier,
            factory = GaugesFragmentLayoutBinding::inflate
        )

    }


    val window = (context as Activity).window
    LaunchedEffect(GaugesAppBarState.isFullScreen) {
        toggleSystemBarsVisibility(
            context = context,
            window = window,
            isVisible = GaugesAppBarState.isFullScreen.not()
        )
    }

    BackHandler {
        // Make system bars visible:
        toggleSystemBarsVisibility(
            context = context,
            window = window,
            isVisible = true
        )
        navigateBack()
    }

    if (GaugesAppBarState.showExitFullScreenSnackbar) {
        val fullScreenHint = stringResource(R.string.fullscreenHint)
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = fullScreenHint
            )
            GaugesAppBarState.showExitFullScreenSnackbar = false
        }

    }
    if (GaugesAppBarState.showTryAgainSnackbar) {
        val tryAgainLabel = stringResource(R.string.try_again)
        val message = stringResource(R.string.bus_busy)
        LaunchedEffect(snackbarHostState) {
            if (snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Long,
                    actionLabel = tryAgainLabel
                ) == SnackbarResult.ActionPerformed
            ) {
                GaugesAppBarState.tryAgain()
            }
            GaugesAppBarState.showTryAgainSnackbar = false
        }
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

fun toggleSystemBarsVisibility(context: Context, window: Window, isVisible: Boolean) {
    WindowCompat.setDecorFitsSystemWindows((context as Activity).window, isVisible)

    val windowInsetsController =
        WindowCompat.getInsetsController(window, window.decorView)
    // Configure the behavior of the hidden system bars.
    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_DEFAULT


    if (isVisible) {
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    } else {
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}