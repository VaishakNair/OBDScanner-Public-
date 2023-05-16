package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Ignition Timing Advance
 * for #1 Cylinder
 */
class Parameter0E(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "0E1\r", gaugeUnit = "Spark Advance(°)",
    ticks = listOf(-64f, -44f, -24f, 0f, 24f, 44f, 64f),
    min = -64f, max = 64f,
    parameterName = "Spark advance", gaugeType = gaugeType
) {

    init {
        unit = "°"
    }

    override fun calculateValue(response: Float) {
        value = response / 2f

        Log.i(APP_NAME, "Ignition timing advance for #1 cylinder ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.ign_timing_advance_1)} ($unit): $valueString"
    }
}