package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import android.util.Log
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Location of Oxygen Sensors
 */
class Parameter13(context: Context) : OtherBaseParameter(
    context, pid = "131\r"
) {

    val sensorList = mutableListOf<String>()

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
            calculateValue(Integer.parseInt(responseFiltered.substring(processResponseStartIndex), 16).toFloat())
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

            if (intValue != 0) {// At least one Oxygen sensor is present.

                if ((intValue and 0b0000_0001) != 0) {
                    sensorList.add(context.getString(R.string.o1_b1))
                }
                if ((intValue and 0b0000_0010) != 0) {
                    sensorList.add(context.getString(R.string.o2_b1))
                }
                if ((intValue and 0b0000_0100) != 0) {
                    sensorList.add(context.getString(R.string.o3_b1))
                }
                if ((intValue and 0b0000_1000) != 0) {
                    sensorList.add(context.getString(R.string.o4_b1))
                }
                if ((intValue and 0b0001_0000) != 0) {
                    sensorList.add(context.getString(R.string.o1_b2))
                }
                if ((intValue and 0b0010_0000) != 0) {
                    sensorList.add(context.getString(R.string.o2_b2))
                }
                if ((intValue and 0b0100_0000) != 0) {
                    sensorList.add(context.getString(R.string.o3_b2))
                }
                if ((intValue and 0b1000_0000) != 0) {
                    sensorList.add(context.getString(R.string.o4_b2))
                }
            }
    }

    override fun calculateValue(response: Float) {
       value = response
    }

    override fun toString(): String {
        return  valueString
    }
}