package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Control module voltage
 */
class Parameter42(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "421\r", gaugeUnit = "Battery/Control Module(V)",
    ticks = listOf(0f, 10f, 20f, 30f, 40f, 50f, 60f, 70f),
    min = 0f, max = 70f,
    parameterName = "Battery/ Control module voltage",
    unitTextSize = 30f, gaugeType = gaugeType
) {

    init {
        unit = "V"
    }


    override fun calculateValue(response: Float) {
        value = response * 0.001f

        Log.i(APP_NAME, "Battery/ Control module voltage ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.battery_ecu_voltage)} ($unit): $valueString"
    }
}