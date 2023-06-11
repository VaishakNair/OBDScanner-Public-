package `in`.v89bhp.obdscanner.ui.scan

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.enums.HandlerMessageCodes
import `in`.v89bhp.obdscanner.helpers.ElmHelper
import `in`.v89bhp.obdscanner.helpers.ScalingFactors
import `in`.v89bhp.obdscanner.helpers.Utilities
import `in`.v89bhp.obdscanner.helpers.Utilities.splitMultilineResponse
import `in`.v89bhp.obdscanner.obdparameters.other.OtherBaseParameter
import `in`.v89bhp.obdscanner.obdparameters.other.Parameter13
import `in`.v89bhp.obdscanner.room.AppRoomDatabase
import `in`.v89bhp.obdscanner.room.ParameterIdRepository

import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.obdparameters.SupportedPidsHolder
import `in`.v89bhp.obdscanner.room.entities.OtherParameterId

class ScanOtherViewModel(application: Application) : AndroidViewModel(application) {
    private val _fetching: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _otherData: MutableLiveData<List<String>> = MutableLiveData()


    val otherDataList: MutableList<String> = mutableListOf()

    var oxygenSensorList: List<String> = listOf()

    private val _otherDataMap: MutableMap<String, String> = mutableMapOf()

    val otherDataMap: Map<String, String>
        get() = _otherDataMap

    var errorMessage: String? = null

    private var stopSending: Boolean = false

    val fetching: LiveData<Boolean>
        get() = _fetching

    private var protocolFetched = false

    private var oxygenSensorTypeFetched = false

    var oxygenSensorType = ""

    private var vinFetched = false

    private var currentIndex: Int = 0

    private val _isError: MutableLiveData<Boolean> = MutableLiveData(false)

    val isError: LiveData<Boolean>
        get() = _isError


    private val repository: ParameterIdRepository by lazy {
        val dao = AppRoomDatabase.getDatabase(application).parameterIdDao()
        ParameterIdRepository(dao)
    }

    private lateinit var otherPids: List<OtherParameterId>



    /**
     * Load pid list from room db and start communication with ELM327
     */
    fun loadOtherData() {
        viewModelScope.launch {
            _isError.value = false
            _fetching.value = true

            ScalingFactors.initialized = false
            protocolFetched = false
            vinFetched = false
            oxygenSensorTypeFetched = false
            oxygenSensorType = ""


            // Remove any items already present in the list/ map (Occurs when a configuration change happens after the list has been populated)
            otherDataList.clear()
            _otherDataMap.clear()

            otherPids = withContext(Dispatchers.IO) {
                repository.otherPidsSynchronous
            }
            fetchOtherData()
        }
    }

    /**
     * Request current powertrain diagnostic data (Service 01 of SAEJ1979 specification)
     */
    private fun fetchOtherData() {

        if(!ScalingFactors.initialized) {// Scaling factors for some PIDs have not been initialized.Initialize them
            ElmHelper.send(mHandler, "${ScalingFactors.PID_FOUR_F}\r")
            return
        }

        if(!protocolFetched) {
            ElmHelper.send(mHandler, "ATDPN\r")
            return
        }

        if(!vinFetched) {
            ElmHelper.send(mHandler, "0902\r")
            return
        }

        if(!oxygenSensorTypeFetched) {
            fetchOxygenSensorType()
            oxygenSensorTypeFetched = true
        }

        when {

            currentIndex < otherPids.size -> ElmHelper.send(mHandler, "01${otherPids[currentIndex].pid}\r")

            else -> {// End of parameter list reached. Show result
                currentIndex = 0
                _otherData.value = otherDataList
                otherDataList.associateByTo(_otherDataMap,
                    {it.substringBefore(':').trim()},
                    {it.substringAfter(':').trim()})
                _fetching.value = false
            }
        }
    }

    /** Handles responses from worker threads */
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            val response: String = msg?.obj as String
            when (msg?.what) {
                HandlerMessageCodes.MESSAGE_ERROR.ordinal -> {
                    errorMessage = response
                    _fetching.value = false
                    _isError.value = true
                }


                HandlerMessageCodes.MESSAGE_RESPONSE_SCALING_FACTORS.ordinal -> {
                    Log.i(APP_NAME, "ScanOtherViewModel: Scaling factors response>$response<")
                    if(ScalingFactors.processScalingFactorsResponse(response, this) && !stopSending) fetchOtherData()
                }

                HandlerMessageCodes.MESSAGE_RESPONSE_AT_COMMAND.ordinal -> {
                    Log.i(APP_NAME, "ScanOtherViewModel: AT command response>$response<")

                    when(ElmHelper.lastCommand ) {
                        "ATDPN" -> {
                            val responseFiltered = response.filter {// Remove spaces and carriage returns
                                // For automatic protocol detection ELM327 will prefix response with 'A'.
                                it != ' ' && it != '\r' && it.isDigit()
                            }

                            try {
                                _otherDataMap[application.applicationContext.getString(R.string.obd_protocol)] = Utilities.getOBDProtocol(responseFiltered.toInt())
                            }
                            catch(ex: NumberFormatException) {
                                Log.i(APP_NAME, ex.toString())
                            }

                            protocolFetched = true

                            if(!stopSending) fetchOtherData()
                        }
                        // TODO Add handling logic for other 'AT' commands. Move the if(!stopSending) code accordingly.
                    }
                }

                HandlerMessageCodes.MESSAGE_RESPONSE_VIN.ordinal ->{
                    Log.i(APP_NAME, "ScanOtherViewModel: VIN command response>$response<")

                    val responseWithoutSpaces = response.filter {
                        // Remove spaces
                        it != ' '
                    }

                    if (responseWithoutSpaces.contains("NODATA").not() && responseWithoutSpaces.startsWith("7F").not()) {// Vehicle
                        // VIN obtained successfully
                        parseVin(responseWithoutSpaces)?.let{
                            _otherDataMap[application.applicationContext.getString(R.string.vin)] = it
                        }
                    }

                    vinFetched = true

                    if(!stopSending) fetchOtherData()
                }

                HandlerMessageCodes.MESSAGE_RESPONSE.ordinal -> {

                    val obdParameter = Class.forName(
                        "`in`.v89bhp.obdscanner.obdparameters.other.Parameter${otherPids[currentIndex].classNameSuffix
                        }"
                    ).constructors[0].newInstance(
                        application.applicationContext
                    ) as OtherBaseParameter

                    obdParameter.processResponse(response)

                    if (obdParameter is Parameter13) { // Location of Oxygen sensors. Returns a list instead of a string.
                        oxygenSensorList = obdParameter.sensorList
                    } else {
                        obdParameter.toString().let {
                            if (it.contains(Utilities.NO_DATA, true).not()) otherDataList.add(it)
                        }
                    }

                    currentIndex++

                    if (!stopSending) fetchOtherData()

                }
            }
        }

        private fun parseVin(responseWithoutSpaces: String): String? {
            // Parse and obtain VIN based on protocol (Page 224 of SAEJ1979)
            return when(Utilities.getOBDProtocolNumber(_otherDataMap[application.applicationContext.getString(R.string.obd_protocol)] ?: "Unknown")) {
                1, 2, 3, 4, 5 -> parseVinNonCan(responseWithoutSpaces)
                6, 7, 8, 9 -> parseVinCan(responseWithoutSpaces)
                else -> null
            }

        }

        /**
         * Parse and return VIN for non-CAN OBD protocols.
         */
        private fun parseVinNonCan(responseWithoutSpaces: String): String? {
            val sb = StringBuilder()
            try {
                val responseLines = splitMultilineResponse(responseWithoutSpaces)

                // Order response lines based on message sequence number (in message count field)
                val responseLinesOrdered = responseLines.sortedBy {
                    it.substring(4, 6).toInt(16)
                }

                // First character of VIN:
                populateVinChars(responseLinesOrdered[0], sb, 12)

                // Remaining characters of VIN (each line supplies 4 characters):
                responseLinesOrdered.subList(1, responseLinesOrdered.size).let {
                    for(line in it) {
                        populateVinChars(line, sb, 6)
                    }
                }

            } catch(ex: NumberFormatException) {
                Log.i(APP_NAME, ex.toString())
                return null
            } catch(ex: IndexOutOfBoundsException) {
                Log.i(APP_NAME, ex.toString())
                return null
            }
            return sb.toString()
        }

        /**
         * Parse and return VIN for CAN protocols.
         */
        private fun parseVinCan(responseWithoutSpaces: String): String? {
            val sb = StringBuilder()
            try {
                val responseLines = splitMultilineResponse(responseWithoutSpaces)

                // Ignore the first line. It contains the number of bytes in the response
                responseLines.subList(1, responseLines.size).let {canResponseLines ->
                    // Order response lines based on message sequence number (the one before ':')
                    val responseLinesOrdered = canResponseLines.sortedBy {
                        it.substring(0, 1).toInt(16)
                    }

                    // First three VIN chars:
                    populateVinChars(responseLinesOrdered[0], sb, 8)

                    // Remaining characters of VIN (each line supplies 7 characters):
                    responseLinesOrdered.subList(1, responseLinesOrdered.size).let {
                        for(line in it) {
                            populateVinChars(line, sb, 2)
                        }
                    }
                }
            } catch(ex: NumberFormatException) {
                Log.i(APP_NAME, ex.toString())
                return null
            } catch(ex: IndexOutOfBoundsException) {
                Log.i(APP_NAME, ex.toString())
                return null
            }
            return sb.toString()
        }

        /**
         * Extract pairs of hex characters, convert them to ascii equivalent and
         * append to ***sb*** starting from ***offset*** till end of line
         */
        private fun populateVinChars(line: String, sb: StringBuilder, startOffset: Int) {
            for(i in startOffset..(line.length-2) step 2) {
                sb.append(line.substring(i, i+2).toInt(16).toChar())
            }
        }

    }

    private fun fetchOxygenSensorType() {
        var narrowband = getApplication<Application>().applicationContext.getString(R.string.narrowband)
        var wideband = getApplication<Application>().applicationContext.getString(R.string.wideband)

        with(SupportedPidsHolder) {
            if(contains("14"))                    oxygenSensorType = getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_1, 1, narrowband)
            if(contains("15"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_1, 2, narrowband)
            if(contains("16"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_1, 3, narrowband)
            if(contains("17"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_1, 4, narrowband)

            if(contains("18"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_2, 1, narrowband)
            if(contains("19"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_2, 2, narrowband)
            if(contains("1A"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_2, 3, narrowband)
            if(contains("1B"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_2, 4, narrowband)

            if(contains("24"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_1, 1, wideband)
            if(contains("25"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_1, 2, wideband)
            if(contains("26"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_1, 3, wideband)
            if(contains("27"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_1, 4, wideband)

            if(contains("28"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_2, 1, wideband)
            if(contains("29"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_2, 2, wideband)
            if(contains("2A"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_2, 3, wideband)
            if(contains("2B"))                    oxygenSensorType += getApplication<Application>().applicationContext.getString(R.string.oxygen_sensor_type_bank_2, 4, wideband)

        }
    }

    override fun onCleared() {
        stopSending = true
        viewModelScope.cancel() // Cancel any coroutines running in this scope
        super.onCleared()
    }
}