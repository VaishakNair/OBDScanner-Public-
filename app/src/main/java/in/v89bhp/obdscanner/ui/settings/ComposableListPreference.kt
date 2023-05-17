package `in`.v89bhp.obdscanner.ui.settings

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ComposableListPreference(
    sharedPrefs: SharedPreferences,
    title: String,
    key: String,
    defaultValue: String,
    entries: List<String>,
    entryValues: List<String>,
    modifier: Modifier = Modifier
) {

    var showDialog by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier
        .padding(16.dp)
        .clickable { showDialog = true }
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = entries[entryValues.indexOf(sharedPrefs.getString(key, defaultValue)!!)],
            style = MaterialTheme.typography.bodySmall
        )
    }
    if (showDialog) {

    }

}