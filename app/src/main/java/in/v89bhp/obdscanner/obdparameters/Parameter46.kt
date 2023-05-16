package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Ambient air temperature
 */
class Parameter46(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "461\r", gaugeUnit = "Ambient Air Temp(°C)",
    ticks = listOf(-40f, 0f, 40f, 80f, 120f, 160f, 200f, 240f),
    min = -40f, max = 240f,
    parameterName = "Ambient air temperature",
    unitTextSize = 36f, gaugeType = gaugeType
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
            gauge.unit = "Ambient Air Temp(°F)"
            unitScalingFactor = 1.8f
            unitScalingFactor1 = 32
            gauge.maxSpeed = 480f
            gauge.ticks = listOf(-40f, 0f, 40f, 80f, 120f, 160f, 200f, 240f,
                280f, 320f, 360f, 400f, 440f, 480f)
            unit = "°F"
        } else {
            unit = "°C"
        }
    }

    override fun calculateValue(response: Float) {
        value = ((response - 40f) * unitScalingFactor) + unitScalingFactor1

        Log.i(APP_NAME, "Ambient air temperature ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.ambient_air_temperature)} ($unit): $valueString"
    }
}