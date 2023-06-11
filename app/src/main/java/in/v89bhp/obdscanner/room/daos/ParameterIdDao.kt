package `in`.v89bhp.obdscanner.room.daos

import androidx.room.Dao
import androidx.room.Query
import `in`.v89bhp.obdscanner.room.entities.OtherParameterId
import `in`.v89bhp.obdscanner.room.entities.ParameterId


@Dao
interface ParameterIdDao {

    @Query("select *, rowid from parameter_id where nameResourceEntryName like :first or descriptionResourceEntryName like :first order by nameResourceEntryName asc")
    fun filterPids(first: String): List<ParameterId>

    @Query("select *, rowid from parameter_id order by nameResourceEntryName asc")
    fun getPidsSynchronous(): List<ParameterId>

    @Query("select *, rowid from other_parameter_id order by name asc")
    fun getOtherPidsSynchronous(): List<OtherParameterId>

}