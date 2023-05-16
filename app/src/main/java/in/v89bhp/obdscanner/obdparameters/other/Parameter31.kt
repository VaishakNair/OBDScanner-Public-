package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import androidx.preference.PreferenceManager
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Distance since diagnostic
 * trouble codes cleared
 */
class Parameter31(context: Context) : OtherBaseParameter(
    context, pid = "311\r"
) {

    /**
     * Scaling factor for unit set by user
     */
    private var unitScalingFactor: Float = 1.0f

    /**
     * String for unit set by user
     */
    private var unit: String

    init {
        if(PreferenceManager.getDefaultSharedPreferences(context).getString(
                Utilities.SettingsPreference.DISTANCE_KEY,
                Utilities.SettingsPreference.DISTANCE_VAL_KM) == Utilities.SettingsPreference.DISTANCE_VAL_MILE) {
            unitScalingFactor = 0.621371f
            unit = "m"
        } else {
            unit = "km"
        }
    }

    override fun calculateValue(response: Float) {
        value = response * unitScalingFactor
    }

    override fun toString(): String {
        return  "${context.getString(R.string.distance_since_dtc_cleared)} ($unit): $valueString"
    }
}