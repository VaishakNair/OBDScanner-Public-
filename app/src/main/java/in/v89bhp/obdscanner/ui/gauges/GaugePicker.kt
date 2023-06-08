package `in`.v89bhp.obdscanner.ui.gauges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.GaugePickerFragmentLayoutBinding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaugePicker(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(stringResource(id = R.string.gauge_picker))
            },
            actions = {


            },
            navigationIcon = {
                IconButton(onClick = {
                    navigateBack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
    }) { contentPadding ->
        Column(modifier = modifier.padding(contentPadding)) {
            Row(
                modifier = modifier
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search),
                    modifier = Modifier.padding(start = 16.dp),
                )
                TextField(
                    value = GaugesAppBarState.searchTextFieldValue,
                    onValueChange = {
                        GaugesAppBarState.searchTextFieldValue = it
                        GaugesAppBarState.filterPids()
                    },
                    label = { Text("Label") }
                )
//                Text(
//                    text = "Search",
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(16.dp),
//                    style = MaterialTheme.typography.bodyMedium,
//                )

            }
            AndroidViewBinding(
                factory = GaugePickerFragmentLayoutBinding::inflate
            ) {
            }
        }
    }

    LaunchedEffect(navigateBack) {
        GaugesAppBarState.navigateBack = navigateBack
    }
}