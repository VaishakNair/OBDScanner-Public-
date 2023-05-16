package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Catalyst Temperature Bank 1, Sensor 1
 */
class Parameter3C(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "3C1\r", gaugeUnit = "Catalyst Temp - B1S1(°C)",
    ticks = listOf(-40f, 1000f, 2000f, 3000f, 4000f, 5000f, 6600f),
    min = -40f, max = 6600f, unitTextSize = 33f,
    parameterName = "Catalyst temperature - B1S1", gaugeType = gaugeType
) {

    /**
     * Scaling factor for unit set by user
     */
    private var unitScalingFactor: Float = 1.0f
    private var unitScalingFactor1: Int = 0


    init {
        if(PreferenceManager.getDefaultSharedPreferences(context).getString(
                Utilities.SettingsPreference.TEMPERATURE_KEY,
                Utilities.SettingsPreference.TEMPERATURE_VAL_CELSIUS) == Utilities.SettingsPreference.TEMPERATURE_VAL_FAHRENHEIT) {
            gauge.unit = "Catalyst Temp - B1S1(°F)"
            unitScalingFactor = 1.8f
            unitScalingFactor1 = 32
            gauge.maxSpeed = 12000f
            gauge.ticks = listOf(-40f, 2000f, 4000f, 6000f, 8000f, 10000f, 12000f)
            unit = "°F"
        } else {
            unit = "°C"
        }
    }

    override fun calculateValue(response: Float) {
        value = (((response * 0.1f) - 40f) * unitScalingFactor) + unitScalingFactor1

        Log.i(APP_NAME, "Catalyst temperature (Bank 1 Sensor 1) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.cat_temp_b1_s1)} ($unit): $valueString"
    }
}