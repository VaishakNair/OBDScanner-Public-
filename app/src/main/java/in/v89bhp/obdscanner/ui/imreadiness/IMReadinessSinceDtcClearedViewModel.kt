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



class IMReadinessSinceDtcClearedViewModel(application: Application) : AndroidViewModel(application) {
    private var _loading by mutableStateOf(false)

    private val _monitorStatuses = mutableListOf<DtcMonitorStatus>().toMutableStateList()

    val monitorStatuses: List<DtcMonitorStatus>
    get() = _monitorStatuses

    private val monitorStatusesList: MutableList<DtcMonitorStatus> = mutableListOf()


    var errorMessage: String? = null

    private var stopSending: Boolean = false

    val loading: Boolean
        get() = _loading


    private var _isError by mutableStateOf(false)

    val isError: Boolean
        get() = _isError


    /**
     * Set necessary fields for UX and start communication with ELM327
     */
    fun loadMonitorStatuses() {
        if(_loading.not()) {
            _isError = false
            _loading = true

            // Remove any items already present in the list (Occurs when a configuration change happens after the list has been populated)
            _monitorStatuses.clear()
            monitorStatusesList.clear()

            fetchMonitorStatuses()
        }

    }

    /**
     * Request monitor statuses since DTC cleared (Service 01, PID 01 of SAEJ1979 specification)
     */
    private fun fetchMonitorStatuses() {

        ElmHelper.send(mHandler, "01011\r")
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
                    // See documentation of 'ElmHelper.liveDataServiceJustExited'
                    if(ElmHelper.liveDataServiceJustExited) {// LiveDataService just exited. So ignore the first response
                        // as it might have been requested by LiveDataService. Then resend the command
                        Log.i(TAG, "Ignoring response")
                        ElmHelper.liveDataServiceJustExited = false
                        fetchMonitorStatuses()
                        return
                    }

                    val responseFiltered = response.filter {
                        // Remove spaces and carriage returns
                        it != ' ' && it != '\r'
                    }

                    if (responseFiltered.contains("NODATA") || responseFiltered.startsWith("7F")) {
                        errorMessage = application.getString(R.string.ign_off_error)
                        _loading = false
                        _isError = true
                        return
                    }

                    populateList(responseFiltered)

                    _monitorStatuses.addAll(monitorStatusesList)
                    _loading = false

                }
            }
        }

        private fun populateList(responseFiltered: String) {
            val byteB: Int = Integer.parseInt(responseFiltered.substring(6, 8), 16)

            if((byteB and 0x01) != 0) {// Misfire monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.misfire),
                    if((byteB and 0x10) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }

            if((byteB and 0x02) != 0) {// Fuel system monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.fuel_system),
                    if((byteB and 0x20) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }

            if((byteB and 0x04) != 0) {// Comprehensive component monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.comprehensive_componenet),
                    if((byteB and 0x40) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }



            val byteC: Int = Integer.parseInt(responseFiltered.substring(8,10), 16)
            val byteD: Int = Integer.parseInt(responseFiltered.substring(10, 12), 16)

            if((byteC and 0x01) != 0) {// Catalyst monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.catalyst),
                    if((byteD and 0x01) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }

            if((byteC and 0x02) != 0) {// Heated Catalyst monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.heated_catalyst),
                    if((byteD and 0x02) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }

            if((byteC and 0x04) != 0) {// Evap System monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.evaporative_system),
                    if((byteD and 0x04) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }

            if((byteC and 0x08) != 0) {// Secondary Air System monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.secondary_air_system),
                    if((byteD and 0x08) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }


            if((byteC and 0x10) != 0) {// A/C System Refrigerant monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.a_c_sys_refrigerant),
                    if((byteD and 0x10) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }

            if((byteC and 0x20) != 0) {// Oxygen Sensor monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.oxygen_sensor),
                    if((byteD and 0x20) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }

            if((byteC and 0x40) != 0) {// Oxyge Sensor Heater monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.oxygen_sensor_heater),
                    if((byteD and 0x40) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }

            if((byteC and 0x80) != 0) {// EGR System monitoring is supported
                monitorStatusesList.add(DtcMonitorStatus(getString(R.string.egr_sys),
                    if((byteD and 0x80) == 0) getString(R.string.complete) else getString(R.string.incomplete)))
            }
        }

    }

    override fun onCleared() {
        stopSending = true
        super.onCleared()
    }

    private fun getString (id: Int): String {
        return getApplication<Application>().applicationContext.getString(id)
    }

    private companion object {
        private const val TAG = "IMReadinessSinceDtcClearedViewModel"
    }
}