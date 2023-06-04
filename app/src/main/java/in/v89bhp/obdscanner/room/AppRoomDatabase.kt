package `in`.v89bhp.obdscanner.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import `in`.v89bhp.obdscanner.room.daos.ParameterIdDao
import pw.softwareengineer.v89bhp.room.entities.OtherParameterId
import pw.softwareengineer.v89bhp.room.entities.ParameterId

// TODO Update database version here and in the packaged database.
@Database(entities = [ParameterId::class, OtherParameterId::class], version = 2)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun parameterIdDao(): ParameterIdDao

    companion object {
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AppRoomDatabase::class.java,
                        "app_database"
                    ).createFromAsset("databases/89bhp.db")
                        .fallbackToDestructiveMigration()
                        .build()

                }
            }
            return INSTANCE as AppRoomDatabase
        }
    }
}