package `in`.v89bhp.obdscanner.ui.gauges

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.findFragment
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.GaugePickerFragmentLayoutBinding
import `in`.v89bhp.obdscanner.databinding.GaugeTypePickerFragmentLayoutBinding
import `in`.v89bhp.obdscanner.fragments.GaugePickerFragment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaugePicker(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(stringResource(id = R.string.app_name))
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
        AndroidViewBinding(modifier = Modifier.padding(contentPadding),
            factory = GaugePickerFragmentLayoutBinding::inflate) {
        }
    }

    LaunchedEffect(navigateBack) {
        GaugesAppBarState.navigateBack = navigateBack
    }
}