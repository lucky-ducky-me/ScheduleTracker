package com.example.scheduletrackervyatsu.data

import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.dao.VyatsuParser
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeachersDepartmentCrossRef
import com.example.scheduletrackervyatsu.data.parsing_models.LessonParsingModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class ScheduleTrackerRepository(
    private val scheduleTrackerDao: ScheduleTrackerDao,
    private val parser:VyatsuParser = VyatsuParser()) {
    suspend fun insert(name: String): Unit {
        scheduleTrackerDao.insert(DepartmentEntity(name = name))
    }

    val departments: Flow<List<DepartmentEntity>> = scheduleTrackerDao.getAllDepartments()

    val teachers: Flow<List<TeacherEntity>> = scheduleTrackerDao.getAllTeachers()

    val trackedTeachersDepartments: Flow<Map<TeacherEntity, List<DepartmentEntity>>> =
        scheduleTrackerDao.getTrackedTeachersDepartments()

    val trackingTeachers: Flow<List<TeacherEntity>> = scheduleTrackerDao.getTrackingTeachers()


    suspend fun insertTeacher(name: String, surname: String, patronymic: String? = null) {
        scheduleTrackerDao.insert(TeacherEntity(
            name = name,
            surname = surname,
            patronymic = patronymic))
    }

    suspend fun insertTrackingForTeacher(teacherId: String, departmentId: String) {
        val teacher: TeacherEntity = scheduleTrackerDao.getTeacher(teacherId)
            ?: throw IllegalArgumentException("Преподавателя с id $teacherId не существует.")

        val department: DepartmentEntity = scheduleTrackerDao.getDepartment(departmentId)
            ?: throw IllegalArgumentException("Кафедры с id $departmentId не существует.")

        scheduleTrackerDao.insert(TeachersDepartmentCrossRef(
            teacherId = teacherId,
            departmentId = departmentId))
    }

    suspend fun deleteTeacher(teacherId: String) {
        scheduleTrackerDao.delete(
            TeacherEntity(teacherId = teacherId, name = "", surname = "", patronymic = null)
        )
    }

    suspend fun deleteTrackingForTeacher(teacherId: String, departmentId: String) {
        val teacher: TeacherEntity = scheduleTrackerDao.getTeacher(teacherId)
            ?: throw IllegalArgumentException("Преподавателя с id $teacherId не существует.")

        val department: DepartmentEntity = scheduleTrackerDao.getDepartment(departmentId)
            ?: throw IllegalArgumentException("Кафедры с id $departmentId не существует.")

        scheduleTrackerDao.delete(TeachersDepartmentCrossRef(teacherId, departmentId))
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

    suspend fun getTrackingTeacherDepartments(teacherId: String): Flow<List<DepartmentEntity>> {
        return scheduleTrackerDao.getTrackingTeacherDepartments(teacherId)
    }


    //region Парсинг

    suspend fun saveSchedule() {

        val lastValue = scheduleTrackerDao.getTrackedTeachersDepartmentsNowFlow()

        var teachers = lastValue.keys

        var departments = mutableSetOf<DepartmentEntity>()

        lastValue.values.forEach {
            departments.addAll(it)
        }

        var teacherNameForParsing = {
                teacher: TeacherEntity ->
            (teacher.surname + " "+
                    "${teacher.name[0]}."+
                    if (teacher.patronymic != null)
                        "${teacher.patronymic!![0]}."
                    else ""
                    ).trim()
        }

        var teacherShortcuts =
            teachers.associate { Pair(it.fio, teacherNameForParsing(it)) }

        var actualSchedule = parser.getActualSchedule(
            teachers = teachers.map { teacherNameForParsing(it) },
            departments = departments.map { it.name },
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(14),
        )

        var schedule = teachers.map {
            Pair(it.teacherId,
                actualSchedule.lessons.getOrDefault(teacherShortcuts[it.fio], emptyList()))
        }

        var lessonEntities = schedule.map {
            fromLessonParsingModelsToEntities(it.second, departments, it.first)
        }
            .flatten().filter {
                val teacher = teachers.find {
                        teacher -> teacher.teacherId == it.teacherId }

                if (teacher != null) {
                    val trackingDepartments = lastValue[teacher]

                    if (trackingDepartments != null) {
                        trackingDepartments.find {
                                department -> it.departmentId == department.departmentId
                        } != null
                    }
                    else {
                        false
                    }
                }
                else {
                    false
                }
            }

        lessonEntities.forEach {
           scheduleTrackerDao.insert(it)
        }

    }

    private fun fromLessonParsingModelsToEntities(
        lessons: List<LessonParsingModel>,
        departments: MutableSet<DepartmentEntity>,
        teacherId: String): List<LessonEntity>
    {
        return lessons.mapNotNull {
            val department = departments.find {
                department -> department.name == it.department
            }

            if (department != null) {
                val date = it.date // Example LocalDate
                val time = it.time // Example LocalTime

                val combinedDateTime: LocalDateTime = LocalDateTime.of(date, time)

                LessonEntity(
                    name = it.name,
                    departmentId = department.departmentId,
                    dateTime = combinedDateTime.toString(),
                    teacherId = teacherId
                )
            }
            else
                null
        }

    }

}