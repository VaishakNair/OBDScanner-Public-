package `in`.v89bhp.obdscanner.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "other_parameter_id")
data class OtherParameterId(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowId: Int = 0,
    val name: String,
    val description: String,
    val pid: String,
    val classNameSuffix: String = pid
)