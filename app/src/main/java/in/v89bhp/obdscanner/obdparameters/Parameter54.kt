package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Evap system vapour pressure (wide range)
 */
class Parameter54(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "541\r", gaugeUnit = "Evap Sys. Vap. Pres(Pa)",
    ticks = listOf(-32800f, -22000f, -12000f, 0f, 12000f, 22000f, 32800f),
    min = -32800f, max = 32800f,
    parameterName = "Evap system vapour pressure (wide range)",
    unitTextSize = 35f, gaugeType = gaugeType
) {

    init {
        unit = "Pa"
    }

    override fun calculateValue(response: Float) {
        value = response

        Log.i(APP_NAME, "Evap system vapour pressure (wide range) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.evap_sys_vap_pres_wide_range)} ($unit): $valueString"
    }
}