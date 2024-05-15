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
    private val parser:VyatsuParser = VyatsuParser())
{
    /**
     * Список всех кафедр.
     */
    val departments: Flow<List<DepartmentEntity>> = scheduleTrackerDao.getAllDepartments()

    /**
     * Список всех преподавателей.
     */
    val teachers: Flow<List<TeacherEntity>> = scheduleTrackerDao.getAllTeachers()

    /**
     * Список кафедр отслеживаемых преподавателей.
     */
    val trackedTeachersDepartments: Flow<Map<TeacherEntity, List<DepartmentEntity>>> =
        scheduleTrackerDao.getTrackedTeachersDepartments()

    /**
     * Список отслеживаемых преподавателей.
     */
    val trackingTeachers: Flow<List<TeacherEntity>> = scheduleTrackerDao.getTrackingTeachers()

    /**
     * Установить отслеживание для преподавателя.
     * @param teacherId идентификатор преподавателя.
     * @param departmentId идентификатор кафедры.
     */
    fun insertTrackingForTeacher(teacherId: String, departmentId: String) {
        val teacher: TeacherEntity = scheduleTrackerDao.getTeacher(teacherId)
            ?: throw IllegalArgumentException("Преподавателя с id $teacherId не существует.")

        val department: DepartmentEntity = scheduleTrackerDao.getDepartment(departmentId)
            ?: throw IllegalArgumentException("Кафедры с id $departmentId не существует.")

        scheduleTrackerDao.insert(TeachersDepartmentCrossRef(
            teacherId = teacherId,
            departmentId = departmentId))
    }

    /**
     * Удалить отслеживание для преподавателя для конкретной кафедры.
     * @param teacherId идентификатор преподавателя.
     * @param departmentId идентификатор кафедры.
     */
    fun deleteTrackingForTeacher(teacherId: String, departmentId: String) {
        val teacher: TeacherEntity = scheduleTrackerDao.getTeacher(teacherId)
            ?: throw IllegalArgumentException("Преподавателя с id $teacherId не существует.")

        val department: DepartmentEntity = scheduleTrackerDao.getDepartment(departmentId)
            ?: throw IllegalArgumentException("Кафедры с id $departmentId не существует.")

        scheduleTrackerDao.delete(TeachersDepartmentCrossRef(teacherId, departmentId))
    }

    /**
     * Удалить отслеживание для преподавателя.
     * @param teacherId идентификатор преподавателя.
     */
    fun deleteTrackingForTeacher(teacherId: String) {
        val teacher: TeacherEntity = scheduleTrackerDao.getTeacher(teacherId)
            ?: throw IllegalArgumentException("Преподавателя с id $teacherId не существует.")

        scheduleTrackerDao.deleteAllTrackingForTeacher(teacherId)
    }

    //TODO DELETE
    fun getAll() {
        scheduleTrackerDao.getAllScheduleChanges()
        scheduleTrackerDao.getAllDepartments()
        scheduleTrackerDao.getAllLessons()
        scheduleTrackerDao.getAllTeachers()
        scheduleTrackerDao.getAllChangeStatus()
        scheduleTrackerDao.getDepartmentWithTeachers()
        scheduleTrackerDao.getTeacherWithDepartments()

    }

    /**
     * Получить кафедры у отслеживаемого преподавателя.
     * @param teacherId идентификатор преподавателя.
     */
    fun getTrackingTeacherDepartments(teacherId: String): Flow<List<DepartmentEntity>> {
        return scheduleTrackerDao.getTrackingTeacherDepartments(teacherId)
    }

    fun getLessons(teacherId: String, departmentId: String): List<LessonEntity>{
        return scheduleTrackerDao.getLessons(teacherId, departmentId)
    }


    //region Парсинг

    fun saveSchedule() {

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