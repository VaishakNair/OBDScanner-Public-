package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Commanded Evaporative Purge
 */
class Parameter2E(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "2E1\r", gaugeUnit = "Evaporative Purge Flow(%)",
    ticks = listOf(0f, 20f, 40f, 60f, 80f, 100f),
    min = 0f, max = 100f, unitTextSize = 30f,
    parameterName = "Evaporative purge flow", gaugeType = gaugeType
) {

    init {
        unit = "%"
    }

    override fun calculateValue(response: Float) {
        value = (response * 100) / 255

        Log.i(APP_NAME, "Evaporative purge flow ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.evaporative_purge_flow)} ($unit): $valueString"
    }
}