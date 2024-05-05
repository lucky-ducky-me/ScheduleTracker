package com.example.scheduletrackervyatsu.data

import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ScheduleTrackerRepository(private val scheduleTrackerDao: ScheduleTrackerDao) {
    suspend fun insert(name: String): Unit {
        scheduleTrackerDao.insert(DepartmentEntity(name = name))
    }

    val departments: Flow<List<DepartmentEntity>> = scheduleTrackerDao.getAllDepartments()


    suspend fun getAll() {

        scheduleTrackerDao.getAllScheduleChanges()
        scheduleTrackerDao.getAllDepartments()
        scheduleTrackerDao.getAllLessons()
        scheduleTrackerDao.getAllTeachers()
        scheduleTrackerDao.getAllChangeStatus()
        scheduleTrackerDao.getDepartmentWithTeachers()
        scheduleTrackerDao.getTeacherWithDepartments()

    }
}