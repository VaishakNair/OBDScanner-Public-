package `in`.v89bhp.obdscanner.obdparameters.other

import android.content.Context
import android.util.Log
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities

/**
 * Fuel system status
 */
class Parameter03(context: Context) : OtherBaseParameter(
    context, pid = "031\r"
) {

    private var byteA = 0
    private var byteB = 0

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
            byteA = Integer.parseInt(
                responseFiltered.substring(
                    processResponseStartIndex,
                    processResponseStartIndex + 2
                ), 16
            )
            byteB = Integer.parseInt(
                responseFiltered.substring(
                    processResponseStartIndex + 2,
                    processResponseStartIndex + 4
                ), 16
            )

        } catch (ex: NumberFormatException) {
            Log.e(APP_NAME, ex.message ?: ex.toString())
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.e(APP_NAME, ex.message ?: ex.toString())
        }


        if (byteA == 0 && byteB == 0) {
            valueString = context.getString(R.string.fuel_status_not_running)
            return
        }


        val sb = StringBuilder()


        if (byteA != 0) {
            sb.append("${context.getString(R.string.fuel_status_1)}: ${getStatus(byteA)}\n")
        }

        if (byteB != 0) {
            sb.append("${context.getString(R.string.fuel_status_2)}: ${getStatus(byteB)}\n")
        }

        valueString = sb.trim().toString()

    }

    override fun calculateValue(response: Float) {
        // Do nothing
    }

    private fun getStatus(byte: Int): String =
        context.getString(
            when {
                (byte and 0b0000_0001) != 0 ->
                    R.string.fuel_system_open_loop

                (byte and 0b0000_0010) != 0 ->
                    R.string.fuel_system_closed_loop

                (byte and 0b0000_0100) != 0 ->
                    R.string.fuel_system_open_loop_driving_cond

                (byte and 0b0000_1000) != 0 ->
                    R.string.fuel_system_open_loop_fault

                (byte and 0b0001_0000) != 0 ->
                    R.string.fuel_system_closed_loop_fault

                else -> R.string.no_data
            }
        )


    override fun toString(): String {
        return valueString
    }
}