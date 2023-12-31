package `in`.v89bhp.obdscanner.ui.scan

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.FreezeFrameFragmentLayoutBinding
import `in`.v89bhp.obdscanner.fragments.FreezeFrameFragment
import `in`.v89bhp.obdscanner.fragments.FreezeFrameState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreezeFrame(
    obdCode: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(stringResource(id = R.string.freeze_frame))
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
        AndroidViewBinding(
            modifier = Modifier.padding(contentPadding),
            factory = FreezeFrameFragmentLayoutBinding::inflate
        ) {
            FreezeFrameState.freezeFrameFragment.obdCode = obdCode
        }
    }
}