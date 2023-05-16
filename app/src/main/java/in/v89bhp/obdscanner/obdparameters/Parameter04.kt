package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Engine Load
 */
class Parameter04(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid ="041\r", gaugeUnit = "Load(%)",
    ticks = listOf(0f, 10f, 20f, 30f, 40f, 50f, 60f, 70f, 80f, 90f, 100f),
    min = 0f,
    max = 100f,
    parameterName = "Engine load", gaugeType = gaugeType
) {

    init {
        unit = "%"
    }

    override fun calculateValue(response: Float) {
        value = (response * 100) / 255f

        Log.i(APP_NAME, "Engine load: $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.engine_load)} ($unit): $valueString"
    }
}