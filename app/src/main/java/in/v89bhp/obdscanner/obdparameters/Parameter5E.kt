package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Engine fuel rate
 */
class Parameter5E(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "5E1\r", gaugeUnit = "Fuel Rate(L/h)",
    ticks = listOf(0f, 500f, 1000f, 1500f, 2000f, 2500f, 3000f, 3500f),
    min = 0f, max = 3500f,
    parameterName = "Engine fuel rate", gaugeType = gaugeType
) {

    init {
        unit = "L/h"
    }


    override fun calculateValue(response: Float) {
        value = response * 0.05f

        Log.i(APP_NAME, "Engine fuel rate ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.engine_fuel_rate)} ($unit): $valueString"
    }
}