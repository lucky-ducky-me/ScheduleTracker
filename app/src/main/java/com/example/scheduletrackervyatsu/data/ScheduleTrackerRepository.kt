package com.example.scheduletrackervyatsu.data

import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeachersDepartmentCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ScheduleTrackerRepository(private val scheduleTrackerDao: ScheduleTrackerDao) {
    suspend fun insert(name: String): Unit {
        scheduleTrackerDao.insert(DepartmentEntity(name = name))
    }

    val departments: Flow<List<DepartmentEntity>> = scheduleTrackerDao.getAllDepartments()

    val settings: Flow<Map<TeacherEntity, List<DepartmentEntity>>> = scheduleTrackerDao.getSettings()

    fun insertTeacher(name: String, surname: String, patronymic: String? = null) {
        scheduleTrackerDao.insert(TeacherEntity(
            name = name,
            surname = surname,
            patronymic = patronymic))
    }

    fun insertTrackingForTeacher(teacherId: String, departmentId: String) {
        val teacher: TeacherEntity = scheduleTrackerDao.getTeacher(teacherId)
            ?: throw IllegalArgumentException("Преподавателя с id $teacherId не существует.")

        val department: DepartmentEntity = scheduleTrackerDao.getDepartment(departmentId)
            ?: throw IllegalArgumentException("Кафедры с id $departmentId не существует.")

        scheduleTrackerDao.insert(TeachersDepartmentCrossRef(
            teacherId = teacherId,
            departmentId = departmentId))
    }

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