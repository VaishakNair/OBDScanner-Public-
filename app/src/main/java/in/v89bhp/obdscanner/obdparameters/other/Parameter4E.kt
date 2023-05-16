package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import `in`.v89bhp.obdscanner.R

/**
 * Engine Run Time since diagnostic
 * trouble codes cleared
 */
class Parameter4E(context: Context) : OtherBaseParameter(
    context, pid = "4E1\r"
) {

    override fun calculateValue(response: Float) {
        value = response
    }

    override fun toString(): String {
        return  "${context.getString(R.string.time_run_since_dtc_cleared)}: $valueString"
    }
}