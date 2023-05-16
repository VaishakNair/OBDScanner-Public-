package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import android.util.Log
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Engine run time
 */
class Parameter7F(context: Context) : OtherBaseParameter(
    context, pid = "7F1\r"
) {

    override fun processResponse(response: String) {
        val responseFiltered = response.filter {
            // Remove spaces and carriage returns
            it != ' ' && it != '\r'
        }

        if (responseFiltered.contains("NODATA") || responseFiltered.startsWith("7F")) {
            valueString = Utilities.NO_DATA
            return
        }

        try {
            calculateValue(Integer.parseInt(responseFiltered.substring(processResponseStartIndex, processResponseStartIndex + 2), 16).toFloat())
        } catch(ex: NumberFormatException) {
            Log.e(APP_NAME, ex.message ?: ex.toString())
            value = 0f
            return
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.e(APP_NAME, ex.message ?: ex.toString())
            value = 0f
            return
        }
        val intValue = value.toInt()

        if(intValue != 0) {// At least engine run time is supported.
            val sb = StringBuilder()

            if ((intValue and 0b0000_0001) != 0) {// Total engine run time supported
                val totalEngineRunTimeSecs = Integer.parseInt(responseFiltered.substring(6, 14), 16)
                sb.append("${context.getString(R.string.engine_total_run_time)}: %.2f\n".format(totalEngineRunTimeSecs / (60f * 60)))
            }
            if ((intValue and 0b0000_0010) != 0) {// Total idle run time supported
                val totalIdleRunTimeSecs = Integer.parseInt(responseFiltered.substring(14, 22), 16)
                sb.append("${context.getString(R.string.engine_total_idle_time)}: %.2f\n".format(totalIdleRunTimeSecs / (60f * 60)))
            }

            valueString = sb.trim().toString()
        }

    }

    override fun calculateValue(response: Float) {
        value = response
    }

    override fun toString(): String {
        return  valueString
    }
}