package `in`.v89bhp.obdscanner.ui.imreadiness

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.*
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.enums.HandlerMessageCodes
import `in`.v89bhp.obdscanner.helpers.ElmHelper

import java.util.*


class IMReadinessDrivingCycleViewModel(application: Application) : AndroidViewModel(application) {
    private var _loading by mutableStateOf(false)

    private val _monitorStatuses = mutableListOf<DtcMonitorStatus>().toMutableStateList()

    val monitorStatuses: List<DtcMonitorStatus>
        get() = _monitorStatuses

    private val monitorStatusesList: MutableList<DtcMonitorStatus> = mutableListOf()


    var errorMessage: String? = null

    private var stopSending: Boolean = false

    val loading: Boolean
        get() = _loading

    private var fetchedSupportedMonitors = false


    private var _isError by mutableStateOf(false)

    val isError: Boolean
        get() = _isError

    private val supportedMonitorsBitSet = BitSet(11)


    /**
     * Set necessary fields for UX and start communication with ELM327
     */
    fun loadMonitorStatuses() {
        if(_loading.not()) {
            _isError = false
            _loading = true

            fetchedSupportedMonitors = false

            // Remove any items already present in the list (Occurs when a configuration change happens after the list has been populated)
            monitorStatusesList.clear()
            _monitorStatuses.clear()

            fetchMonitorStatuses()
        }

    }

    /**
     * Request monitor statuses for current driving cycle (Service 01, PID 41 of SAEJ1979 specification)
     */
    private fun fetchMonitorStatuses() {
        if (!fetchedSupportedMonitors) {
            ElmHelper.send(mHandler, "01011\r")
            return
        }

        ElmHelper.send(mHandler, "01411\r")
    }

    /** Handles responses from worker threads */
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            val response: String = msg?.obj as String
            when (msg?.what) {
                HandlerMessageCodes.MESSAGE_ERROR.ordinal -> {
                    Log.i(TAG, "Error received.")
                    errorMessage = response
                    _loading = false
                    _isError = true
                }


                HandlerMessageCodes.MESSAGE_RESPONSE.ordinal -> {
                    val responseFiltered = response.filter {
                        // Remove spaces and carriage returns
                        it != ' ' && it != '\r'
                    }


                    if (responseFiltered.contains("NODATA") || responseFiltered.startsWith("7F")) {
                        errorMessage = application.getString(R.string.engine_off_not_supported)
                        _loading = false
                        _isError = true
                        return
                    }

                    if (ElmHelper.lastCommand == "01011") {// Result contains supported monitors
                        setSupportedMonitors(responseFiltered)
                        fetchedSupportedMonitors = true
                        if (!stopSending) fetchMonitorStatuses()
                        return
                    }

                    populateList(responseFiltered)
                    _monitorStatuses.addAll(monitorStatusesList)
                    _loading = false
                }
            }
        }

        private fun setSupportedMonitors(responseFiltered: String) {
            val byteB: Int = Integer.parseInt(responseFiltered.substring(6, 8), 16)

            if ((byteB and 0x01) != 0) {// Misfire monitoring is supported
                supportedMonitorsBitSet.set(0)
            }

            if ((byteB and 0x02) != 0) {// Fuel system monitoring is supported
                supportedMonitorsBitSet.set(1)
            }

            if ((byteB and 0x04) != 0) {// Comprehensive component monitoring is supported
                supportedMonitorsBitSet.set(2)
            }


            val byteC: Int = Integer.parseInt(responseFiltered.substring(8, 10), 16)

            if ((byteC and 0x01) != 0) {// Catalyst monitoring is supported
                supportedMonitorsBitSet.set(3)
            }

            if ((byteC and 0x02) != 0) {// Heated Catalyst monitoring is supported
                supportedMonitorsBitSet.set(4)
            }

            if ((byteC and 0x04) != 0) {// Evap System monitoring is supported
                supportedMonitorsBitSet.set(5)
            }

            if ((byteC and 0x08) != 0) {// Secondary Air System monitoring is supported
                supportedMonitorsBitSet.set(6)
            }


            if ((byteC and 0x10) != 0) {// A/C System Refrigerant monitoring is supported
                supportedMonitorsBitSet.set(7)
            }

            if ((byteC and 0x20) != 0) {// Oxygen Sensor monitoring is supported
                supportedMonitorsBitSet.set(8)
            }

            if ((byteC and 0x40) != 0) {// Oxyge Sensor Heater monitoring is supported
                supportedMonitorsBitSet.set(9)
            }

            if ((byteC and 0x80) != 0) {// EGR System monitoring is supported
                supportedMonitorsBitSet.set(10)
            }
        }

        private fun populateList(responseFiltered: String) {
            val byteB: Int = Integer.parseInt(responseFiltered.substring(6, 8), 16)


            addMonitorStatus(getString(R.string.misfire), 0, byteB, 0x01, byteB, 0x10)

            addMonitorStatus(getString(R.string.fuel_system), 1, byteB, 0x02, byteB, 0x20)

            addMonitorStatus(
                getString(R.string.comprehensive_componenet),
                2,
                byteB,
                0x04,
                byteB,
                0x40
            )


            val byteC: Int = Integer.parseInt(responseFiltered.substring(8, 10), 16)
            val byteD: Int = Integer.parseInt(responseFiltered.substring(10, 12), 16)


            addMonitorStatus(getString(R.string.catalyst), 3, byteC, 0x01, byteD, 0x01)

            addMonitorStatus(getString(R.string.heated_catalyst), 4, byteC, 0x02, byteD, 0x02)

            addMonitorStatus(getString(R.string.evaporative_system), 5, byteC, 0x04, byteD, 0x04)

            addMonitorStatus(getString(R.string.secondary_air_system), 6, byteC, 0x08, byteD, 0x08)

            addMonitorStatus(getString(R.string.a_c_sys_refrigerant), 7, byteC, 0x10, byteD, 0x10)

            addMonitorStatus(getString(R.string.oxygen_sensor), 8, byteC, 0x20, byteD, 0x20)

            addMonitorStatus(getString(R.string.oxygen_sensor_heater), 9, byteC, 0x40, byteD, 0x40)

            addMonitorStatus(getString(R.string.egr_sys), 10, byteC, 0x80, byteD, 0x80)

        }

        private fun addMonitorStatus(
            monitorName: String, monitorBitPosition: Int, byte1: Int, byte1Mask: Int, byte2: Int,
            byte2Mask: Int
        ) {
            if (supportedMonitorsBitSet.get(monitorBitPosition)) {// Monitoring is supported
                if ((byte1 and byte1Mask) == 0) {//  Monitoring is disabled
                    monitorStatusesList.add(
                        DtcMonitorStatus(
                            monitorName,
                            getString(R.string.disabled)
                        )
                    )
                } else {
                    monitorStatusesList.add(
                        DtcMonitorStatus(
                            monitorName,
                            getString(if ((byte2 and byte2Mask) == 0) R.string.complete else R.string.incomplete)
                        )
                    )
                }
            }
        }

    }

    override fun onCleared() {
        stopSending = true
        super.onCleared()
    }

    private fun getString(id: Int): String {
        return getApplication<Application>().applicationContext.getString(id)
    }

    private companion object {
        private const val TAG = "IMReadinessDrivingCycleViewModel"
    }
}