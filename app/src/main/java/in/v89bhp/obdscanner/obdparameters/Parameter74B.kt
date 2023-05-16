package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R

/**
 * Turbocharger RPM (B)
 */
class Parameter74B(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "741\r", gaugeUnit = "Turbo RPM (B) (x1000)",
    ticks = listOf(0f, 5f, 10f, 15f, 20f, 25f, 30f, 35f, 40f),
    min = 0f, max = 40f,
    parameterName = "Turbo RPM (B)",
    unitTextSize = 35f, gaugeType = gaugeType
) {

    init {
        processResponseStartIndex = 10
        processResponseEndIndex = 14

        processFFResponseStartIndex = 12
        processFFResponseEndIndex = 16

        unit = "x1000"
    }

    override fun calculateValue(response: Float) {
        value = response / 1000

        Log.i(APP_NAME, "Turbo RPM (B) ($unit): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.turbo_rpm_b)} ($unit): $valueString"
    }
}