package `in`.v89bhp.obdscanner.ui.settings

import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

const val SHARED_PREF_FILE_NAME = "shared_pref_settings"

@Composable
fun Settings(modifier: Modifier = Modifier) {
    Column() {
        ComposablePreferenceCategory(title = "Units") {
            val sharedPrefs =
                LocalContext.current.getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE)
            ComposableListPreference(
                sharedPrefs = sharedPrefs,
                title = "Distance",
                key = "distance",
                defaultValue = "km",
                entries = listOf("Kilometre", "Mile"),
                entryValues = listOf("km", "m")
            )

            ComposableListPreference(
                sharedPrefs = sharedPrefs,
                title = "Temperature",
                key = "temperature",
                defaultValue = "c",
                entries = listOf("Celsius", "Fahrenheit"),
                entryValues = listOf("c", "f")
            )

            ComposableListPreference(
                sharedPrefs = sharedPrefs,
                title = "Pressure",
                key = "pressure",
                defaultValue = "p",
                entries = listOf("PSI", "Bar"),
                entryValues = listOf("p", "b")
            )
        }

    }

}