package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.ScalingFactors

/**
 * Mass air flow rate
 */
class Parameter10(context: Context, viewModel: ViewModel?, gaugeType: String? = null) : BaseParameter(
    context, viewModel, pid = "101\r", gaugeUnit = "MAF(g/s)",
    ticks = listOf(0f, 100f, 200f, 300f, 400f, 500f, 600f, 700f),
    min = 0f, max = 700f,
    parameterName = "MAF rate", gaugeType = gaugeType

) {

    init {
        unit = "g/s"
    }

    private var initialized = false

    override fun processResponse(response: String) {
        if(!initialized) {
            if (ScalingFactors.mafScalingFactor != 0.01f) {
                gauge.ticks = listOf(0f, 500f, 1000f, 1500f, 2000f, 2500f, 2600f)
                gauge.maxSpeed = 2600f
            }
            initialized = true
        }

        super.processResponse(response)
    }

    override fun calculateValue(response: Float) {
        value = response * ScalingFactors.mafScalingFactor

        Log.i(APP_NAME, "Mass air flow rate: $value")
    }

    override fun toString(): String {
        return "${context.getString(R.string.maf_rate)} ($unit): $valueString"
    }
}