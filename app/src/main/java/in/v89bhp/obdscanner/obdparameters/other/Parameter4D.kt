package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import `in`.v89bhp.obdscanner.R

/**
 * Time run by the engine while MIL is
 * activated
 */
class Parameter4D(context: Context) : OtherBaseParameter(
    context, pid = "4D1\r"
) {

    override fun calculateValue(response: Float) {
        value = response
    }

    override fun toString(): String {
        return  "${context.getString(R.string.mil_engine_run_time)}: $valueString"
    }
}