package com.example.scheduletrackervyatsu.data

import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleTrackerRepository(private val scheduleTrackerDao: ScheduleTrackerDao) {
    suspend fun insert(name: String): Unit {
        withContext(Dispatchers.IO) {

        }
    }

    suspend fun getAll() {
        withContext(Dispatchers.IO) {
            scheduleTrackerDao.getAllScheduleChanges()
            scheduleTrackerDao.getAllDepartments()
            scheduleTrackerDao.getAllLessons()
            scheduleTrackerDao.getAllTeachers()
            scheduleTrackerDao.getAllChangeStatus()
            scheduleTrackerDao.getDepartmentWithTeachers()
            scheduleTrackerDao.getTeacherWithDepartments()
        }
    }
}