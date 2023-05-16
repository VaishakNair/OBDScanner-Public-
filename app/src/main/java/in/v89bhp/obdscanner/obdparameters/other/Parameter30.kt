package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import `in`.v89bhp.obdscanner.R

/**
 * Number of warm-ups since
 * diagnostic trouble codes cleared
 */
class Parameter30(context: Context) : OtherBaseParameter(
    context, pid = "301\r"
) {

    override fun calculateValue(response: Float) {
        value = response
    }

    override fun toString(): String {
        return  "${context.getString(R.string.warm_ups_since_dtc_cleared)}: $valueString"
    }
}