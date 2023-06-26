package `in`.v89bhp.obdscanner.ui.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "HomeViewModel"
        val HOME_ITEM = NavigationDestination.GAUGES // TODO Set appropriate home item.
    }


    var selectedItem by mutableStateOf(HOME_ITEM)

// TODO

}