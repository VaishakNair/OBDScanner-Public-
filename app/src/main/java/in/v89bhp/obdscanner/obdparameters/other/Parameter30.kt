package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities
import java.lang.StringBuilder

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