package com.example.scheduletrackervyatsu.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.entities.LessonEntity

@Database(
    version = 1,
    entities = [
        LessonEntity::class,
    ]
)
abstract class ScheduleTrackerDatabase: RoomDatabase() {

    abstract fun getScheduleTrackerDao(): ScheduleTrackerDao

    companion object {
        @Volatile
        private var _instance: ScheduleTrackerDatabase? = null

        fun getDatabase(context: Context): ScheduleTrackerDatabase {
            val tempInstance = _instance

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                _instance = Room.databaseBuilder(
                    context.applicationContext
                    , ScheduleTrackerDatabase::class.java
                    , "scheduleTracker.db")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate the database with initial data
                            // For example:
                            val scheduleTrackerDao = _instance?.getScheduleTrackerDao()
                            //scheduleTrackerDao?.insert(User("John Doe"))
                            //scheduleTrackerDao?.insert(User("Jane Smith"))Log.d("db", "Типо заполнили департмаентами")
                        }
                    }).build()

                return _instance as ScheduleTrackerDatabase
            }
        }
    }
}

fun getDepartments(): List<String> {
    return emptyList()
}