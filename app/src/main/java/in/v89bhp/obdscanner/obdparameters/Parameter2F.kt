package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Fuel level input
 */
class Parameter2F(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "2F1\r", gaugeUnit = "Fuel Level(%)",
    ticks = listOf(0f, 20f, 40f, 60f, 80f, 100f),
    min = 0f, max = 100f,
    parameterName = "Fuel level", gaugeType = gaugeType
) {

    init {
        unit = "%"
    }


    override fun calculateValue(response: Float) {
        value = (response * 100) / 255
        Log.i(APP_NAME, "Fuel level ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.fuel_level)} ($unit): $valueString"
    }
}