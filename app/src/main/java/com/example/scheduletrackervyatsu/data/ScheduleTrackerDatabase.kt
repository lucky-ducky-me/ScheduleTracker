package com.example.scheduletrackervyatsu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.scheduletrackervyatsu.DATABASE_NAME
import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.entities.ChangeStatusEntity
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.ScheduleChangeEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeachersDepartmentCrossRef

@Database(
    version = 1,
    entities = [
        ChangeStatusEntity::class,
        DepartmentEntity::class,
        LessonEntity::class,
        ScheduleChangeEntity::class,
        TeacherEntity::class,
        TeachersDepartmentCrossRef::class
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
                    , DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val scheduleTrackerDao = _instance?.getScheduleTrackerDao()

                            //todo Заполенине кафедр из файла
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