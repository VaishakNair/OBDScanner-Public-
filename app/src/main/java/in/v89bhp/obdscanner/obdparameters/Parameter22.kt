package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Fuel rail pressure relative to manifold vacuum.
 */
class Parameter22(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "221\r", gaugeUnit = "Mani. Vac. Fuel Pres(PSI)",
    ticks = listOf(0f, 100f, 200f, 300f, 400f, 500f, 600f, 700f),
    min = 0f, max = 700f,
    parameterName = "Manifold vacuum fuel pressure",
    unitTextSize = 32f, gaugeType = gaugeType
) {

    /**
     * Scaling factor for unit set by user
     */
    private var unitScalingFactor: Float = 1.0f

    init {
        if(PreferenceManager.getDefaultSharedPreferences(context).getString(
                Utilities.SettingsPreference.PRESSURE_KEY,
                Utilities.SettingsPreference.PRESSURE_VAL_PSI) == Utilities.SettingsPreference.PRESSURE_VAL_BAR) {
            gauge.unit = "Mani. Vac. Fuel Pres(Bar)"
            unitScalingFactor = 0.0689476f
            gauge.maxSpeed = 50f
            gauge.ticks = listOf(0f, 10f, 20f, 30f, 40f, 50f)
            unit = "Bar"
        } else {
            unit = "PSI"
        }
    }

    override fun calculateValue(response: Float) {
        value = response * 0.079f * 0.1450377f * unitScalingFactor

        Log.i(APP_NAME, "Fuel rail pressure relative to manifold vacuum ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.frp_mani_vac)} ($unit): $valueString"
    }
}