package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Evap system vapour pressure
 */
class Parameter32(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "321\r", gaugeUnit = "Evap Sys. Vap. Pres(Pa)",
    ticks = listOf(-8200f, -6000f, -4000f, -2000f, 0f, 2000f, 4000f, 6000f, 8200f),
    min = -8200f, max = 8200f,
    parameterName = "Evap system vapour pressure",
    unitTextSize = 35f, gaugeType = gaugeType
) {

    init {
        unit = "Pa"
    }

    override fun calculateValue(response: Float) {
        value = response * 0.25f

        Log.i(APP_NAME, "Evap system vapour pressure ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.evap_system_vap_pres)} ($unit): $valueString"
    }
}