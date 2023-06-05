package `in`.v89bhp.obdscanner.ui.gauges

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import `in`.v89bhp.obdscanner.databinding.GaugeTypePickerFragmentLayoutBinding
import `in`.v89bhp.obdscanner.databinding.GaugesFragmentLayoutBinding

@Composable
fun Gauges(modifier: Modifier = Modifier) {
    AndroidViewBinding(
        factory = GaugesFragmentLayoutBinding::inflate) {
//        val myFragment = fragmentContainerView. // TODO Find instance to the fragment.

    }
}