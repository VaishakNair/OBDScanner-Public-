package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Short term fuel trim (Bank 2 Sensor 3)
 */
class Parameter1A(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "1A1\r", gaugeUnit = "STFT - B2S3(%)",
    ticks = listOf(-100f, -80f, -60f, -40f, -20f, 0f, 20f, 40f, 60f, 80f, 100f),
    min = -100f, max = 100f,
    parameterName = "STFT - B2S3", gaugeType = gaugeType
) {

    init {
        // Data byte B contains the fuel trim value:
        processResponseStartIndex = 6
        processFFResponseStartIndex = 8
        unit = "%"
    }




    override fun calculateValue(response: Float) {
        // The response contains 0xFF if the sensor is present but
        // is not used in the fuel trim calculation.
        if(response != 0xFF.toFloat()) value = ((response * 100) / 128) - 100

        Log.i(APP_NAME, "Short term fuel trim (Bank 2 Sensor 3): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.stft_b2_s3)} ($unit): $valueString"
    }
}