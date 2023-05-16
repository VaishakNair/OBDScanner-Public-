package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.anastr.speedviewlib.Gauge
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.ScalingFactors

/**
 * Wide range O2 sensor equivalence ratio (Bank 1 Sensor 4)
 */
class Parameter37EQ(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "371\r", gaugeUnit = "Equivalence Ratio - B1S4",
    ticks = listOf(0f, 0.5f, 1.0f, 1.5f, 2.0f),
    min = 0f, max = 2f,
    parameterName = "Equivalence ratio - B1S4",
    unitTextSize = 33f, gaugeType = gaugeType
) {

    init {
        // Ignore data bytes C, D
        processResponseStartIndex = 4
        processResponseEndIndex = 8
        processFFResponseStartIndex = 6
        processFFResponseEndIndex = 10
        gauge.speedTextFormat = 3
        gauge.tickTextFormat = Gauge.FLOAT_FORMAT.toInt()
        unit = ""
    }

    private var initialized = false


    override fun processResponse(response: String) {
        if(!initialized) {
            if (ScalingFactors.maxEquivalenceRatio != 2.0f) {
               gauge.maxSpeed = ScalingFactors.maxEquivalenceRatio
               gauge.ticks =  ScalingFactors.maxEquivalenceRatio.let {
                   val list = mutableListOf<Float>()
                   for (i in 0..it.toInt()) {
                       list.add(i.toFloat())
                   }
                   list
               }

            }
            initialized = true
        }

        super.processResponse(response)
    }

    override fun calculateValue(response: Float) {
        value = response * ScalingFactors.equivalenceRatioScalingFactor

        Log.i(APP_NAME, "Equivalence ratio (Bank 1 Sensor 4): $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.equi_ratio_b1_s4)}: $valueString"
    }
}