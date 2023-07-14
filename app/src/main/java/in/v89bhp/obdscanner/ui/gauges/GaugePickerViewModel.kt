package `in`.v89bhp.obdscanner.ui.gauges

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import `in`.v89bhp.obdscanner.room.AppRoomDatabase
import `in`.v89bhp.obdscanner.room.ParameterIdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import `in`.v89bhp.obdscanner.room.entities.ParameterId

class GaugePickerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ParameterIdRepository by lazy {
        val dao = AppRoomDatabase.getDatabase(application).parameterIdDao()
        ParameterIdRepository(dao)
    }

    val pids: MutableLiveData<List<ParameterId>?> = MutableLiveData()

    private lateinit var allPids: List<ParameterId>


    fun filterPids(query: String) {

        Log.i(TAG, "Query received: >>$query<<")

        viewModelScope.launch {

            pids.value = withContext(Dispatchers.IO) {
                //                repository.filterPids("%$query%")
                val filteredIds = mutableListOf<ParameterId>()
                var name: String
                var description: String
                allPids.filterTo(filteredIds) {
                   name = getString(it.nameResourceEntryName)
                    description = getString(it.descriptionResourceEntryName)
                    name.contains(query, ignoreCase = true) || description.contains(query, ignoreCase = true)
                }
                filteredIds
            }
        }
    }

    fun loadPids() {
        viewModelScope.launch {

            allPids = withContext(Dispatchers.IO) {
                repository.pidsSynchronous
            }

            pids.value = allPids
        }
    }


    private fun getString(resourceEntryName: String): String =
        getApplication<Application>().run{
            getString(resources.getIdentifier(resourceEntryName, "string", packageName))}


    private companion object {
        private const val TAG = "GaugePickrViewMdl"
    }

}


