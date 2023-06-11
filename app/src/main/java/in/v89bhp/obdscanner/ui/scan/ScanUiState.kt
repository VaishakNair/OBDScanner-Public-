package `in`.v89bhp.obdscanner.ui.scan

import androidx.compose.runtime.*
import `in`.v89bhp.obdscanner.fragments.ScanTroubleCodesFragment

object ScanUiState {

    lateinit var scanTroubleCodesFragment: ScanTroubleCodesFragment

    var showClearTroubleCodesDialog by mutableStateOf(false)

    fun clearTroubleCodes() {
        showClearTroubleCodesDialog = false
        scanTroubleCodesFragment.clearTroubleCodes()
    }
}