package `in`.v89bhp.obdscanner.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.GaugeTypePickerFragmentLayoutBinding
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaugeTypePicker(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier) {
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
            factory = GaugeTypePickerFragmentLayoutBinding::inflate) {
//        val myFragment = fragmentContainerView. // TODO Find instance to the fragment.

        }
    }
}