package pw.softwareengineer.v89bhp.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey


@Entity(tableName = "parameter_id")
data class ParameterId(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowId: Int = 0,
    val nameResourceEntryName: String,
    val descriptionResourceEntryName: String,
    val pid: String,
    val classNameSuffix: String = pid
)