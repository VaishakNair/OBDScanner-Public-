package `in`.v89bhp.obdscanner.ui.scan

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import `in`.v89bhp.obdscanner.databinding.ScanFragmentLayoutBinding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Scan(modifier: Modifier = Modifier) {
    AndroidViewBinding(
        modifier = modifier,
        factory = ScanFragmentLayoutBinding::inflate
    ) {

    }
}