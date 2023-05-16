package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Fuel injection timing
 */
class Parameter5D(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "5D1\r", gaugeUnit = "Fuel Injection Timing(°)",
    ticks = listOf(-250f, -200f, -150f, -100f, -50f, 0f, 50f, 100f, 150f, 200f, 250f, 300f, 350f),
    min = -250f, max = 350f,
    parameterName= "Fuel injection timing",
    unitTextSize = 35f, gaugeType = gaugeType
) {

    init {
        unit = "°"
    }


    override fun calculateValue(response: Float) {
        value = (response / 128) - 302.0703125f

        Log.i(APP_NAME, "Fuel injection timing ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.fi_timing)} ($unit): $valueString"
    }
}