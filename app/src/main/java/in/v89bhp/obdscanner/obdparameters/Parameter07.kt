package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Long term fuel trim (Bank 1)
 */
class Parameter07(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "071\r", gaugeUnit = "LTFT - B1(%)",
    ticks = listOf(-100f, -80f, -60f, -40f, -20f, 0f, 20f, 40f, 60f, 80f, 100f),
    min = -100f, max = 100f,
    parameterName = "LTFT - B1", gaugeType = gaugeType
) {

    init {
        // Ignore data byte B:
        processResponseEndIndex = 6
        processFFResponseEndIndex = 8
        unit = "%"
    }




    override fun calculateValue(response: Float) {
        value = ((response * 100) / 128) - 100

        Log.i(APP_NAME, "Long term fuel trim (Bank 1): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.ltft_b1)} ($unit): $valueString"
    }
}