package `in`.v89bhp.obdscanner.helpers


import android.util.Log
import java.util.*

object Utilities {


    // Constants:
    const val NO_DATA = "NO DATA"
    const val NO_DATA_COUNT_THRESHOLD = 15
    const val UNABLE_TO_CONNECT = "UNABLE TO CONNECT"
    const val BUFFER_FULL = "BUFFER FULL"
    const val BUS_ERROR = "BUS ERROR"
    const val CAN_ERROR = "CAN ERROR"
    const val DATA_ERROR = "DATA ERROR"
    const val BUS_BUSY = "BUS BUSY"
    const val SPEED_TO_DURATION = 270L

    // Settings preference keys:
    object SettingsPreference {
        const val PRESSURE_KEY = "pressure"
        const val PRESSURE_VAL_PSI = "p"
        const val PRESSURE_VAL_BAR = "b"

        const val DISTANCE_KEY = "distance"
        const val DISTANCE_VAL_KM = "km"
        const val DISTANCE_VAL_MILE = "m"

        const val TEMPERATURE_KEY = "temperature"
        const val TEMPERATURE_VAL_CELSIUS = "c"
        const val TEMPERATURE_VAL_FAHRENHEIT = "f"


    }


    fun splitMultilineResponse(response: String): MutableList<String> {
        Log.i(BuildConfig.APP_NAME, "Splitting lines")
        return response.lines().filter { it.contains('\r').not() && it.isNotEmpty() }
            .toMutableList()
    }


    fun decodeSupportedPIDs(line: String, starting: Int, supportedPIDs: MutableSet<String>): Int {

        // Data byte A:
        var byte = Integer.parseInt(line.substring(4, 6), 16)
        extractBitEncodedPID(byte, starting, supportedPIDs)

        // Data byte B:
        byte = Integer.parseInt(line.substring(6, 8), 16)
        extractBitEncodedPID(byte, starting + 8, supportedPIDs)

        // Data byte C:
        byte = Integer.parseInt(line.substring(8, 10), 16)
        extractBitEncodedPID(byte, starting + 16, supportedPIDs)

        // Data byte D:
        byte = Integer.parseInt(line.substring(10, 12), 16)
        extractBitEncodedPID(byte, starting + 24, supportedPIDs)
        Log.i("YOLO", "Data byte D: $byte")

        return if ((byte and 0b0000_0001) != 0) starting + 24 + 8 else 0
    }

    private fun extractBitEncodedPID(byte: Int, starting: Int, supportedPIDs: MutableSet<String>) {
        if ((byte and 0b1000_0000) != 0) supportedPIDs.add("%02X".format(starting + 1))
        if ((byte and 0b0100_0000) != 0) supportedPIDs.add("%02X".format(starting + 2))
        if ((byte and 0b0010_0000) != 0) supportedPIDs.add("%02X".format(starting + 3))
        if ((byte and 0b0001_0000) != 0) supportedPIDs.add("%02X".format(starting + 4))
        if ((byte and 0b0000_1000) != 0) supportedPIDs.add("%02X".format(starting + 5))
        if ((byte and 0b0000_0100) != 0) supportedPIDs.add("%02X".format(starting + 6))
        if ((byte and 0b0000_0010) != 0) supportedPIDs.add("%02X".format(starting + 7))
        if ((byte and 0b0000_0001) != 0) supportedPIDs.add("%02X".format(starting + 8))
    }

    /**
     * Converts protocol number returned by 'AT DPN' to protocol name
     */
    fun getOBDProtocol(protocolNumber: Int): String =
        when (protocolNumber) {
            1 -> "SAE J1850 PWM (41.6 kbaud)"
            2 -> "SAE J1850 VPW (10.4 kbaud)"
            3 -> "ISO 9141-2 (5 baud init, 10.4 kbaud)"
            4 -> "ISO 14230-4 KWP (5 baud init, 10.4 kbaud)"
            5 -> "ISO 14230-4 KWP (fast init, 10.4 kbaud)"
            6 -> "ISO 15765-4 CAN (11 bit ID, 500 kbaud)"
            7 -> "ISO 15765-4 CAN (29 bit ID, 500 kbaud)"
            8 -> "ISO 15765-4 CAN (11 bit ID, 250 kbaud)"
            9 -> "ISO 15765-4 CAN (29 bit ID, 250 kbaud)"
            0xA -> "SAE J1939 CAN (29 bit ID, 250* kbaud)"
            0xB -> "USER1 CAN (11* bit ID, 125* kbaud)"
            0xC -> "USER2 CAN (11* bit ID, 50* kbaud)"
            else -> "Unknown"
        }

    /**
     * Converts protocol name to ELM327 protocol number
     */
    fun getOBDProtocolNumber(protocol: String): Int =
        when (protocol) {
            "SAE J1850 PWM (41.6 kbaud)" -> 1
            "SAE J1850 VPW (10.4 kbaud)" -> 2
            "ISO 9141-2 (5 baud init, 10.4 kbaud)" -> 3
            "ISO 14230-4 KWP (5 baud init, 10.4 kbaud)" -> 4
            "ISO 14230-4 KWP (fast init, 10.4 kbaud)" -> 5
            "ISO 15765-4 CAN (11 bit ID, 500 kbaud)" -> 6
            "ISO 15765-4 CAN (29 bit ID, 500 kbaud)" -> 7
            "ISO 15765-4 CAN (11 bit ID, 250 kbaud)" -> 8
            "ISO 15765-4 CAN (29 bit ID, 250 kbaud)" -> 9
            "SAE J1939 CAN (29 bit ID, 250* kbaud)" -> 0xA
            "USER1 CAN (11* bit ID, 125* kbaud)" -> 0xB
            "USER2 CAN (11* bit ID, 50* kbaud)" -> 0xC
            else -> -1
        }
}