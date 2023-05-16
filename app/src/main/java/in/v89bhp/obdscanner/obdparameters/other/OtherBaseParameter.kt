package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import android.util.Log
import `in`.v89bhp.obdscanner.BuildConfig
import `in`.v89bhp.obdscanner.helpers.Utilities

abstract class OtherBaseParameter(
    val context: Context, val pid: String) {

    /**
     * The actual value of this parameter
     */
    var value: Float = 0f

    var valueString: String = Utilities.NO_DATA

    protected var processResponseStartIndex: Int = 4
    protected var processResponseEndIndex: Int = 0

    /**
     * Called when a response to the request for this parameter
     * is received
     */
    open fun processResponse(response: String) {
        val responseFiltered = response.filter {
            // Remove spaces and carriage returns
            it != ' ' && it != '\r'
        }

        if (responseFiltered.contains("NODATA") || responseFiltered.startsWith("7F")) {
            valueString = Utilities.NO_DATA
            return
        }

        try {
            calculateValue(Integer.parseInt(if(processResponseEndIndex == 0) responseFiltered.substring(processResponseStartIndex)
            else responseFiltered.substring(processResponseStartIndex, processResponseEndIndex), 16).toFloat())
        } catch(ex: NumberFormatException) {
            Log.e(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            value = 0f
            return
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.e(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            value = 0f
            return
        }

        valueString = value.toString()
    }

    /**
     * PID specific calculation logic to be implemented by subclasses.
     */
    abstract fun calculateValue(response: Float)

}