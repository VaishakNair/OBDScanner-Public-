package `in`.v89bhp.obdscanner.room

import `in`.v89bhp.obdscanner.room.daos.ParameterIdDao
import `in`.v89bhp.obdscanner.room.entities.OtherParameterId
import `in`.v89bhp.obdscanner.room.entities.ParameterId

class ParameterIdRepository(private val parameterIdDao: ParameterIdDao) {

    fun filterPids(query: String): List<ParameterId> = parameterIdDao.filterPids(query)

    val pidsSynchronous: List<ParameterId>
        get() = parameterIdDao.getPidsSynchronous()

    val otherPidsSynchronous: List<OtherParameterId>
        get() = parameterIdDao.getOtherPidsSynchronous()


}