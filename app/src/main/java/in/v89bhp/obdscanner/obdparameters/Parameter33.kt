package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Barometric pressure
 */
class Parameter33(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "331\r", gaugeUnit = "Barometric Pres(PSI)",
    ticks = listOf(0f, 10f, 20f, 30f, 40f, 50f),
    min = 0f, max = 50f,
    parameterName = "Barometric pressure",
    unitTextSize = 38f, gaugeType = gaugeType
) {

    /**
     * Scaling factor for unit set by user
     */
    private var unitScalingFactor: Float = 1.0f

    init {
        if(PreferenceManager.getDefaultSharedPreferences(context).getString(
                Utilities.SettingsPreference.PRESSURE_KEY,
                Utilities.SettingsPreference.PRESSURE_VAL_PSI) == Utilities.SettingsPreference.PRESSURE_VAL_BAR) {
            gauge.unit = "Barometric Pres(Bar)"
            unitScalingFactor = 0.0689476f
            gauge.maxSpeed = 4f
            gauge.tickTextFormat = 1
            gauge.ticks = listOf(0f, 0.5f, 1f, 1.5f, 2f, 2.5f, 3f, 3.5f, 4f)
            unit = "Bar"
        } else {
            unit = "PSI"
        }
    }

    override fun calculateValue(response: Float) {
        // Multiplying with 0.145038 to convert kPa to PSI
        value = response* 0.145038f * unitScalingFactor

        Log.i(APP_NAME, "Barometric pressure ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.barometric_pres)} ($unit): $valueString"
    }
}