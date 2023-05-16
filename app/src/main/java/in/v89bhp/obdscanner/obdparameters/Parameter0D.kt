package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Vehicle speed
 */
class Parameter0D(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "0D1\r", gaugeUnit = "km/h",
    ticks = listOf(0f, 20f, 40f, 60f, 80f, 100f, 120f, 140f, 160f, 180f),
    min = 0f, max = 180f,
    parameterName = "Vehicle speed", gaugeType = gaugeType
) {

    /**
     * Scaling factor for unit set by user
     */
    private var unitScalingFactor: Float = 1.0f

    init {
        if(PreferenceManager.getDefaultSharedPreferences(context).getString(
                Utilities.SettingsPreference.DISTANCE_KEY,
                Utilities.SettingsPreference.DISTANCE_VAL_KM) == Utilities.SettingsPreference.DISTANCE_VAL_MILE) {
            gauge.unit = "m/h"
            unitScalingFactor = 0.621371f
            gauge.maxSpeed = 120f
            gauge.ticks = listOf(0f, 20f, 40f, 60f, 80f, 100f, 120f)
            unit = "m/h"
        } else {
            unit = "km/h"
        }
    }

    override fun calculateValue(response: Float) {
        value = response * unitScalingFactor

        Log.i(APP_NAME, "Speed: $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.vehicle_speed)} ($unit): $valueString"
    }
}