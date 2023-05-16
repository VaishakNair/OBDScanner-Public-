package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.ScalingFactors

/**
 * Wide range O2 sensor voltage (Bank 2 Sensor 2)
 */
class Parameter29(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "291\r", gaugeUnit = "Oxygen Sensor - B2S2(V)",
    ticks = listOf(0f, 130f, 260f),
    min = 0f, max = 260f,
    parameterName = "Wide range oxygen sensor - B2S2",
    unitTextSize = 32f, gaugeType = gaugeType
) {

    init {
        // Ignore data bytes A, B
        processResponseStartIndex = 8
        processFFResponseStartIndex = 10
        gauge.speedTextFormat = 3
        unit = "V"
    }

    override fun calculateValue(response: Float) {
        value = response * ScalingFactors.oxygenSensorVoltageScalingFactor

        Log.i(APP_NAME, "Wide range oxygen sensor (Bank 2 Sensor 2) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.wide_range_o2_b2_s2)} ($unit): $valueString"
    }
}