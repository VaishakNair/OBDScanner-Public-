package `in`.v89bhp.obdscanner.ui.scan

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.enums.HandlerMessageCodes
import `in`.v89bhp.obdscanner.helpers.ElmHelper
import `in`.v89bhp.obdscanner.helpers.ScalingFactors
import `in`.v89bhp.obdscanner.helpers.Utilities
import `in`.v89bhp.obdscanner.helpers.Utilities.splitMultilineResponse
import `in`.v89bhp.obdscanner.obdparameters.BaseParameter
import `in`.v89bhp.obdscanner.room.AppRoomDatabase
import `in`.v89bhp.obdscanner.room.ParameterIdRepository
import `in`.v89bhp.obdscanner.room.entities.ParameterId

class FreezeFrameViewModel(application: Application) : AndroidViewModel(application) {
    private val _fetching: MutableLiveData<Boolean> = MutableLiveData(false)
    val fetching: LiveData<Boolean>
        get() = _fetching

    private val _ffData: MutableLiveData<List<String>> = MutableLiveData()
    val ffData: LiveData<List<String>>
        get() = _ffData


    var errorMessage: String? = null

    private var stopSending: Boolean = false

    private var currentIndex: Int = 0

    private val _isError: MutableLiveData<Boolean> = MutableLiveData()

    val isError: LiveData<Boolean>
        get() = _isError

    val ffDataList: MutableList<String> = mutableListOf()

    private val repository: ParameterIdRepository by lazy {
        val dao = AppRoomDatabase.getDatabase(application).parameterIdDao()
        ParameterIdRepository(dao)
    }

    private lateinit var pids: List<ParameterId>



    /**
     * Load pid list from room db and start communication with ELM327
     */
    fun loadFFData() {
        viewModelScope.launch {
            _isError.value = false
            _fetching.value = true

            ScalingFactors.initialized = false

            ffDataList.clear() // Remove any items already present in the list (Occurs when a configuration change happens after the list has been populated)
            pids = withContext(Dispatchers.IO) {
                repository.pidsSynchronous
            }
            fetchFFData()
        }
    }

    /**
     * Request freeze frame diagnostic data (Service 02 of SAEJ1979 specification)
     */
     private fun fetchFFData() {

        if(!ScalingFactors.initialized) {// Scaling factors for some PIDs have not been initialized.Initialize them
            ElmHelper.send(mHandler, "${ScalingFactors.PID_FOUR_F}\r")
            return
        }

        when {

            currentIndex < pids.size -> ElmHelper.send(mHandler, "02${pids[currentIndex].pid}00\r")

            else -> {// End of parameter list reached. Show result
                currentIndex = 0
                _ffData.value = ffDataList
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
                    Log.i(APP_NAME, "FreezeFrameViewModel: Scaling factors response>$response<")
                    if(ScalingFactors.processScalingFactorsResponse(response, this) && !stopSending) fetchFFData()
                }

                HandlerMessageCodes.MESSAGE_RESPONSE.ordinal -> {
                    Log.i(APP_NAME, "FreezeFrameViewModel: Response >$response<")
                    Log.i(APP_NAME, "Command for message response: ${ElmHelper.lastCommand}")


                    val obdParameter = Class.forName(
                        "`in`.v89bhp.obdscanner.obdparameters.Parameter${pids[currentIndex].classNameSuffix
                        }"
                    ).constructors[0].newInstance(
                        application.applicationContext, this@FreezeFrameViewModel, null
                    ) as BaseParameter

                    obdParameter.processFFResponse(response)
                    obdParameter.toString().let {
                        if(it.contains(Utilities.NO_DATA, true).not()) ffDataList.add(it)
                    }

                    currentIndex++

                    if(!stopSending) fetchFFData()
                }
            }
        }
    }

    override fun onCleared() {
        stopSending = true
        viewModelScope.cancel() // Cancel any coroutines running in this scope
        super.onCleared()
    }
}