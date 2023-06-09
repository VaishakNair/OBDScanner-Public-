package `in`.v89bhp.obdscanner.ui.gauges

import androidx.annotation.DrawableRes
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import `in`.v89bhp.obdscanner.fragments.GaugePickerFragment
import `in`.v89bhp.obdscanner.fragments.GaugesFragment

object GaugesAppBarState {
    lateinit var gaugesFragment: GaugesFragment
    lateinit var gaugePickerFragment: GaugePickerFragment

    var showExitFullScreenSnackbar by mutableStateOf(false)

    var showTryAgainSnackbar by mutableStateOf(false)

    var isFullScreen by mutableStateOf(false)

    var navDrawerGesturesEnabled by mutableStateOf(true)

    var searchTextFieldValue by mutableStateOf(TextFieldValue(""))


    fun filterPids() = gaugePickerFragment.filterPids(searchTextFieldValue.text)

    fun onAppBarActionClick(@DrawableRes clickedIconId: Int) {
        gaugesFragment.onAppBarActionClick(clickedIconId)
    }

    fun tryAgain() {
        gaugesFragment.tryAgain()
    }

    lateinit var navigateBack: () -> Unit

    var gaugeSettingsDialogState by mutableStateOf(GaugeSettingsDialogState(false))
}

class GaugeSettingsDialogState(
    val show: Boolean,
    val parameterIndex: Int = 0,
    val title: String = "",
    val label: String = "",
    maxValue: String = "",
    audioAlert: Boolean = false
) {
    var audioAlertThresholdTextFieldValue by mutableStateOf(TextFieldValue(maxValue))
    var audioAlert by mutableStateOf(audioAlert)
}