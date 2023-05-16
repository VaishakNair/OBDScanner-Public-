package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.ScalingFactors
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Wide range O2 sensor current (Bank 1 Sensor 4)
 */
class Parameter37(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "371\r", gaugeUnit = "Oxygen Sensor - B1S4(mA)",
    ticks = listOf(-130f, 0f, 130f, 260f),
    min = -130f, max = 260f,
    parameterName = "Wide range oxygen sensor - B1S4",
    unitTextSize = 30f, gaugeType = gaugeType
) {

    init {
        // Ignore data bytes A, B
        processResponseStartIndex = 8
        processFFResponseStartIndex = 10
        gauge.speedTextFormat = 2
        unit = "mA"
    }

    override fun calculateValue(response: Float) {
        value = response * ScalingFactors.oxygenSensorCurrentScalingFactor

        Log.i(APP_NAME, "Wide range oxygen sensor (Bank 1 Sensor 4) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.wide_range_o2_b1_s4)} ($unit): $valueString"
    }
}