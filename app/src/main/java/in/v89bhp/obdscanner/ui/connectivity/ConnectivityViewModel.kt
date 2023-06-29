package `in`.v89bhp.obdscanner.ui.connectivity

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager

class ConnectivityViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "ConnectivityViewModel"
        private const val BLUETOOTH_PERMISSION_RATIONALE_PREF_KEY = "shouldShowBluetoothPermissionRationale"
    }

    var shouldShowBluetoothPermissionRationale: Boolean
        get() {
            val sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences((getApplication() as Context))
            return sharedPrefs.getBoolean(BLUETOOTH_PERMISSION_RATIONALE_PREF_KEY, false)

        }
        set(shouldShowRationale) {
            val sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences((getApplication() as Context))
            sharedPrefs.edit()
                .putBoolean(BLUETOOTH_PERMISSION_RATIONALE_PREF_KEY, shouldShowRationale).apply()
        }


}