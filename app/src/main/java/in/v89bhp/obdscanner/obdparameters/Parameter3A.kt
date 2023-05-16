package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.ScalingFactors

/**
 * Wide range O2 sensor current (Bank 2 Sensor 3)
 */
class Parameter3A(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "3A1\r", gaugeUnit = "Oxygen Sensor - B2S3(mA)",
    ticks = listOf(-130f, 0f, 130f, 260f),
    min = -130f, max = 260f,
    parameterName = "Wide range oxygen sensor - B2S3",
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

        Log.i(APP_NAME, "Wide range oxygen sensor (Bank 2 Sensor 3) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.wide_range_o2_b2_s3)} ($unit): $valueString"
    }
}