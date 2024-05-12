package com.example.scheduletrackervyatsu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.scheduletrackervyatsu.DATABASE_NAME
import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.dao.VyatsuParser
import com.example.scheduletrackervyatsu.data.entities.ChangeStatusEntity
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.ScheduleChangeEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeachersDepartmentCrossRef
import com.squareup.okhttp.OkHttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = _instance!!.getScheduleTrackerDao()

                                dao.getAllDepartments()
                                dao.getAllTeachers()

                                val parser = VyatsuParser()

                                val teachers = parser.getTeachers().map {
                                    val array = it.split(" ")

                                    if (array.size == 3) {
                                        TeacherEntity(name = array[0], surname = array[1], patronymic = array[2])
                                    }
                                    else {
                                        TeacherEntity(name = array[0], surname = array[1], patronymic = null)
                                    }
                                }

                                teachers.forEach {
                                    dao.insert(it)
                                }

                                val departments = parser.getDepartments().map {
                                    DepartmentEntity(name = it)
                                }

                                departments.forEach {
                                    dao.insert(it)
                                }

                                dao.insert(DepartmentEntity(name = "12321321321"))
                            }

                        }
                     })
                    .fallbackToDestructiveMigration()
                    .build()


                return _instance as ScheduleTrackerDatabase
            }
        }
    }
}

class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)



    }
}