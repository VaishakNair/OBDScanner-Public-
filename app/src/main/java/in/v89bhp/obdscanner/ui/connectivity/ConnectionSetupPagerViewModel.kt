package `in`.v89bhp.obdscanner.ui.connectivity

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.*

class ConnectionSetupPagerViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        const val TAG = "ConnectionSetupPagerViewModel"
    }

    var isNextButtonEnabled by mutableStateOf(true)

}