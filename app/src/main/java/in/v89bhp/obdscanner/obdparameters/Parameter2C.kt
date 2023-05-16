package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Commanded EGR status
 */
class Parameter2C(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "2C1\r", gaugeUnit = "EGR valve flow(%)",
    ticks = listOf(0f, 20f, 40f, 60f, 80f, 100f),
    min = 0f, max = 100f,
    parameterName = "EGR valve flow", gaugeType = gaugeType
) {

    init {
        unit = "%"
    }

    override fun calculateValue(response: Float) {
        value = (response * 100) / 255

        Log.i(APP_NAME, "EGR valve flow ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.egr_valve_flow)} ($unit): $valueString"
    }
}