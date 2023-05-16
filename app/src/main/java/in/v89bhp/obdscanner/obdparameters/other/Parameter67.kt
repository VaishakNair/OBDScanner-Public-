package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import pw.softwareengineer.v89bhp.BuildConfig
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities
import java.lang.StringBuilder

/**
 * Engine coolant temperature sensors supported
 */
class Parameter67(context: Context) : OtherBaseParameter(
    context, pid = "671\r"
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

        if(intValue != 0) {// At least one MAF sensor is supported.
            val sb = StringBuilder()

            if ((intValue and 0b0000_0001) != 0) {
                sb.append("${context.getString(R.string.ect_1_supported)}\n")
            }
            if ((intValue and 0b0000_0010) != 0) {
                sb.append("${context.getString(R.string.ect_2_supported)}\n")
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