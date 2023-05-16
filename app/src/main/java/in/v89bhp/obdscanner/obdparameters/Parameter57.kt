package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Short term fuel trim (Bank 2 Secondary O2 sensor)
 */
class Parameter57(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "571\r", gaugeUnit = "Secondary STFT - B2(%)",
    ticks = listOf(-100f, -80f, -60f, -40f, -20f, 0f, 20f, 40f, 60f, 80f, 100f),
    min = -100f, max = 100f,
    parameterName = "Secondary STFT - B2",
    unitTextSize =  35f, gaugeType = gaugeType
) {

    init {
        // Ignore data byte B:
        processResponseEndIndex = 6
        processFFResponseEndIndex = 8
        unit = "%"
    }




    override fun calculateValue(response: Float) {
        value = ((response * 100) / 128) - 100

        Log.i(APP_NAME, "Secondary Short term fuel trim (Bank 2) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.secondary_stft_b2)} ($unit): $valueString"
    }
}