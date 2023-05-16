package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Absolute throttle position
 */
class Parameter11(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "111\r", gaugeUnit = "Absolute TP(%)",
    ticks = listOf(0f, 10f, 20f, 30f, 40f, 50f, 60f, 70f, 80f, 90f, 100f),
    min = 0f, max = 100f,
    parameterName = "Absolute throttle position", gaugeType = gaugeType
) {

    init {
        unit = "%"
    }

    override fun calculateValue(response: Float) {
        value = (response * 100)/ 255f

        Log.i(APP_NAME, "Absolute throttle position: $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.absolute_throttle_position)} ($unit): $valueString"
    }
}