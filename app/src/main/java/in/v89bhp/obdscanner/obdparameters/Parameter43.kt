package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Absolute Load Value
 */
class Parameter43(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "431\r", gaugeUnit = "Abs. Load Value(%)",
    ticks = listOf(0f, 5000f, 10000f, 15000f, 20000f, 25000f, 30000f),
    min = 0f, max = 30000f,
    parameterName = "Absolute load value", gaugeType = gaugeType
) {

    init {
        unit = "%"
    }


    override fun calculateValue(response: Float) {
        value = (response * 100) / 255

        Log.i(APP_NAME, "Absolute load value ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.abs_load_value)} ($unit): $valueString"
    }
}