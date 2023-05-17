package `in`.v89bhp.obdscanner.ui.settings

import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

const val SHARED_PREF_FILE_NAME = "shared_pref_settings"

@Composable
fun Settings(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 50.dp)) {
        ComposableListPreference(
            sharedPrefs = LocalContext.current.getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE),
            title = "Distance",
            key = "distance",
            defaultValue = "km",
            entries = listOf("Kilometre", "Mile"),
            entryValues = listOf("km", "m")
        )
    }

}