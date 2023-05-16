package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.anastr.speedviewlib.Gauge.FLOAT_FORMAT
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * O2 sensor voltage (Bank 1 Sensor 3)
 */
class Parameter16O2(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "161\r", gaugeUnit = "Oxygen Sensor - B1S3(V)",
    ticks = listOf(0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f, 1.1f, 1.2f, 1.3f),
    min = 0f, max = 1.3f,
    parameterName = "Oxygen sensor - B1S3",
    unitTextSize = 34f, gaugeType = gaugeType
) {

    init {
        // Ignore data byte B
        processResponseEndIndex = 6
        processFFResponseEndIndex = 8
        gauge.speedTextFormat = 3
        gauge.tickTextFormat = FLOAT_FORMAT.toInt()
        unit = "V"
    }




    override fun calculateValue(response: Float) {
        value = response * 0.005f

        Log.i(APP_NAME, "O2 sensor (Bank 1 Sensor 3) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.oxygen_sensor_b1_s3)} ($unit): $valueString"
    }
}