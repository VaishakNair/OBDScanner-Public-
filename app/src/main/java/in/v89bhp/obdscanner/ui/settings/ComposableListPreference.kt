package `in`.v89bhp.obdscanner.ui.settings

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

    Box(modifier = modifier
        .fillMaxWidth()
        .height(80.dp)
        .clickable { showDialog = true }) {
        Column(
            modifier = Modifier.align(Alignment.TopStart)
                .padding(start = 50.dp, top = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = entries[entryValues.indexOf(sharedPrefs.getString(key, defaultValue)!!)],
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(
                    "This area typically contains the supportive text " +
                            "which presents the details regarding the Dialog's purpose."
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Dismiss")
                }
            },
            confirmButton = {}
        )
    }

}