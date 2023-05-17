package `in`.v89bhp.obdscanner.ui.settings

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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

    var summary by remember {
        mutableStateOf(
            entries[entryValues.indexOf(sharedPrefs.getString(key, defaultValue)!!)]
        )
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    Box(modifier = modifier
        .fillMaxWidth()
        .height(80.dp)
        .clickable { showDialog = true }) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 50.dp, top = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title,
            fontSize = 18.sp)
            Text(
                text = summary,
                fontSize = 14.sp
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

                val (selectedOption, onOptionSelected) = remember {
                    mutableStateOf(
                        entries[entryValues.indexOf(
                            sharedPrefs.getString(key, defaultValue)!!
                        )]
                    )
                }

                Column(Modifier.selectableGroup()) {
                    entries.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = {
                                        onOptionSelected(text)
                                        with(sharedPrefs.edit()) {
                                            putString(key, entryValues[entries.indexOf(text)])
                                            apply()
                                        }
                                        summary = text
                                        showDialog = false
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null // null recommended for accessibility with screenreaders
                            )
                            Text(
                                text = text,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
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