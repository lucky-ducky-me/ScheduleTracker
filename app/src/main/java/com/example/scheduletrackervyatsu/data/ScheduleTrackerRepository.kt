package com.example.scheduletrackervyatsu.data

import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleTrackerRepository(private val scheduleTrackerDao: ScheduleTrackerDao) {
    suspend fun insertLesson(name: String): Unit {
        withContext(Dispatchers.IO) {
            scheduleTrackerDao.insertLesson(
                LessonEntity(name = name, surname = "", patronymic = "")
            )
        }
    }

    suspend fun getAllLessons(): List<LessonEntity> {
        return scheduleTrackerDao.getAllLessons()
    }
}