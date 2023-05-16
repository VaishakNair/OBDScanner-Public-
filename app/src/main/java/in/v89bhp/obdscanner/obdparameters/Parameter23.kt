package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Fuel rail pressure (high) for GDI and diesel engines.
 */
class Parameter23(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "231\r", gaugeUnit = "x100 Fuel Pres(PSI)",
    ticks = listOf(0f, 100f, 200f, 300f, 400f, 500f, 600f, 700f, 800f),
    min = 0f, max = 800f,
    parameterName = "Fuel rail pressure", gaugeType = gaugeType
) {

    /**
     * Scaling factor for unit set by user
     */
    private var unitScalingFactor: Float = 1.0f


    init {
        if(PreferenceManager.getDefaultSharedPreferences(context).getString(
                Utilities.SettingsPreference.PRESSURE_KEY,
                Utilities.SettingsPreference.PRESSURE_VAL_PSI) == Utilities.SettingsPreference.PRESSURE_VAL_BAR) {
            gauge.unit = "x100 Fuel Pres(Bar)"
            unitScalingFactor = 0.0689476f
            gauge.maxSpeed = 60f
            gauge.ticks = listOf(0f, 10f, 20f, 30f, 40f, 50f, 60f)
            unit = "x100 Bar"
        } else {
            unit = "x100 PSI"
        }
    }

    override fun calculateValue(response: Float) {
        value = ((response * 10 * 0.1450377f) / 100) * unitScalingFactor

        Log.i(APP_NAME, "Fuel rail pressure (GDI and diesel engines) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.frp_gdi_diesel)} ($unit): $valueString"
    }
}