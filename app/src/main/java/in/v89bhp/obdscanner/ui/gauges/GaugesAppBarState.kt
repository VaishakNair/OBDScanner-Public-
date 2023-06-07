package `in`.v89bhp.obdscanner.ui.gauges

import androidx.annotation.DrawableRes
import androidx.compose.runtime.*
import `in`.v89bhp.obdscanner.fragments.GaugesFragment

object GaugesAppBarState {
    lateinit var gaugesFragment: GaugesFragment

    var showExitFullScreenSnackbar by mutableStateOf(false)

    var isFullScreen by mutableStateOf(false)

    fun onAppBarActionClick(@DrawableRes clickedIconId: Int) {
        gaugesFragment.onAppBarActionClick(clickedIconId)
    }
}