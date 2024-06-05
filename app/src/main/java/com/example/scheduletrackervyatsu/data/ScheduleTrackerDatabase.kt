package com.example.scheduletrackervyatsu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.scheduletrackervyatsu.DATABASE_NAME
import com.example.scheduletrackervyatsu.LESSON_STATUSES
import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.dao.VyatsuParser
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.LessonStatusEntity
import com.example.scheduletrackervyatsu.data.entities.Logs
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeachersDepartmentCrossRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * База данных.
 */
@Database(
    version = 1,
    entities = [
        LessonStatusEntity::class,
        DepartmentEntity::class,
        LessonEntity::class,
        TeacherEntity::class,
        TeachersDepartmentCrossRef::class,
        Logs::class
    ]
)
abstract class ScheduleTrackerDatabase: RoomDatabase() {

    /**
     * Получить DAO базы данных.
     */
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

                                val departments = parser.getDepartments().map {
                                    DepartmentEntity(name = it)
                                }

                                departments.forEach {
                                    dao.insert(it)
                                }

                                val teachers = parser.getTeachers().map {
                                    val teacherFio = it.first.split(" ")
                                    val department = departments.find {
                                        department -> it.second == department.name
                                    }

                                    if (teacherFio.size == 3) {
                                        TeacherEntity(name = teacherFio[1],
                                            surname = teacherFio[0], patronymic = teacherFio[2],
                                            defaultDepartment = department?.departmentId)
                                    }
                                    else {
                                        TeacherEntity(name = teacherFio[1],
                                            surname = teacherFio[0], patronymic = null,
                                            defaultDepartment = department?.departmentId)
                                    }
                                }

                                teachers.forEach {
                                    dao.insert(it)
                                }

                                LESSON_STATUSES.forEach {
                                    dao.insert(it)
                                }
                            }
                        }
                     })
                    .build()


                return _instance as ScheduleTrackerDatabase
            }
        }
    }
}