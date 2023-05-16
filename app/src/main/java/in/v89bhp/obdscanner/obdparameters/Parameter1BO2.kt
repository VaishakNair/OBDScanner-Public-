package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.anastr.speedviewlib.Gauge.FLOAT_FORMAT
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * O2 sensor voltage (Bank 2 Sensor 4)
 */
class Parameter1BO2(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "1B1\r", gaugeUnit = "Oxygen Sensor - B2S4(V)",
    ticks = listOf(0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f, 1.1f, 1.2f, 1.3f),
    min = 0f, max = 1.3f, unitTextSize = 34f,
    parameterName = "Oxygen sensor - B2S4", gaugeType = gaugeType
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

        Log.i(APP_NAME, "O2 sensor (Bank 2 Sensor 4) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.oxygen_sensor_b2_s4)} ($unit): $valueString"
    }
}