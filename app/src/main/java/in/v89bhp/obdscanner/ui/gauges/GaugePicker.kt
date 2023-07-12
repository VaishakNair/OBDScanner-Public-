package `in`.v89bhp.obdscanner.ui.gauges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
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
                    GaugesAppBarState.searchTextFieldValue = TextFieldValue("")
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

            TextField(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search),

                    )
                },
                value = GaugesAppBarState.searchTextFieldValue,
                onValueChange = {
                    GaugesAppBarState.searchTextFieldValue = it
                    GaugesAppBarState.filterPids()
                },
                label = { Text(stringResource(R.string.search)) },
                singleLine = true
            )


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