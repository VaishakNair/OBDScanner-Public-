package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Engine RPM
 */
class Parameter0C(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "0C1\r", gaugeUnit = "x1000 rpm",
    ticks = listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f),
    min = 0f, max = 7f,
    parameterName = "RPM", gaugeType = gaugeType
) {

    init {
        unit = "x1000"
    }


    override fun calculateValue(response: Float) {
        value = response / 4000f

        Log.i(APP_NAME, "RPM ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.rpm)} ($unit): $valueString"
    }
}