package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Boost pressure (B)
 */
class Parameter70B(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "701\r", gaugeUnit = "Boost Pressure (B) (PSI)",
    ticks = listOf(0f, 100f, 200f, 300f, 400f),
    min = 0f, max = 400f,
    parameterName = "Turbo boost pressure (B)",
    unitTextSize = 34f, gaugeType = gaugeType
) {

    /**
     * Scaling factor for unit set by user
     */
    private var unitScalingFactor: Float = 1.0f

    init {
        if(PreferenceManager.getDefaultSharedPreferences(context).getString(
                Utilities.SettingsPreference.PRESSURE_KEY,
                Utilities.SettingsPreference.PRESSURE_VAL_PSI) == Utilities.SettingsPreference.PRESSURE_VAL_BAR) {
            gauge.unit = "Boost Pressure (B) (Bar)"
            unitScalingFactor = 0.0689476f
            gauge.maxSpeed = 30f
            gauge.ticks = listOf(0f, 5f, 10f, 15f, 20f, 25f, 30f)
            unit = "Bar"
        } else {
            unit = "PSI"
        }
    }

    init {
        processResponseStartIndex = 18

        processFFResponseStartIndex = 20
    }

    override fun calculateValue(response: Float) {
        // Multiplying with 0.145038 to convert kPa to PSI
        value = response * 0.03125f * 0.145038f * unitScalingFactor

        Log.i(APP_NAME, "Boost pressure (B) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.boost_pressure_b)} ($unit): $valueString"
    }
}