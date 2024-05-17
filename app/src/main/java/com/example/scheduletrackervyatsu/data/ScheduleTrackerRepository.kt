package com.example.scheduletrackervyatsu.data

import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.dao.VyatsuParser
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeachersDepartmentCrossRef
import com.example.scheduletrackervyatsu.data.helpers.fromLessonParsingModelsToEntities
import com.example.scheduletrackervyatsu.data.helpers.fromLessonParsingModelsToEntity
import com.example.scheduletrackervyatsu.data.helpers.getInitials
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ScheduleTrackerRepository(
    private val scheduleTrackerDao: ScheduleTrackerDao,
    private val parser: VyatsuParser = VyatsuParser())
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

        val department: DepartmentEntity = scheduleTrackerDao.getDepartment(departmentId)

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
        scheduleTrackerDao.delete(TeachersDepartmentCrossRef(teacherId, departmentId))
    }

    /**
     * Удалить отслеживание для преподавателя.
     * @param teacherId идентификатор преподавателя.
     */
    fun deleteTrackingForTeacher(teacherId: String) {
        scheduleTrackerDao.deleteAllTrackingForTeacher(teacherId)
    }

    /**
     * Получить кафедры у отслеживаемого преподавателя.
     * @param teacherId идентификатор преподавателя.
     * @return поток списка занятий.
     */
    fun getTrackingTeacherDepartments(teacherId: String): Flow<List<DepartmentEntity>> {
        return scheduleTrackerDao.getTrackingTeacherDepartments(teacherId)
    }

    /**
     * Получить занятия.
     * @param teacherId идентификатор преподавателя.
     * @param departmentId идентификатор кафедры.
     * @return список занятий.
     */
    fun getLessons(teacherId: String, departmentId: String): List<LessonEntity>{
        return scheduleTrackerDao.getLessons(teacherId, departmentId)
    }

    /**
     * Получить занятия.
     * @return список занятий.
     */
    fun getAllLessons(): List<LessonEntity> {
        return scheduleTrackerDao.getAllLessons()
    }

    fun getTrackedTeachersDepartments(): Map<TeacherEntity, List<DepartmentEntity>> {
        return scheduleTrackerDao.getTrackedTeachersDepartmentsNowFlow()
    }


    //region Функции для DailyReceiver

    fun getDayWeekAndName(day: LocalDate): Pair<Boolean, String>? {
        var ans = scheduleTrackerDao.getDayWeekAndName(day.toString())

        if (ans != null && ans.isNotEmpty()) {
            return Pair(ans[0].week, ans[0].dayOfWeek)
        }

        val prevDay = day.minusDays(14)

        ans = scheduleTrackerDao.getDayWeekAndName(prevDay.toString())

        if (ans != null && ans.isNotEmpty()) {
            return Pair(ans[0].week, ans[0].dayOfWeek)
        }

        return null
    }


    //region Парсинг

    /**
     * Получить и сохранить расписание для всех отлеживаемых преподавателей.
     */
    fun saveSchedule() {
        val trackedTeachersDepartmentsNotFlow = scheduleTrackerDao.getTrackedTeachersDepartmentsNowFlow()

        val teachers = trackedTeachersDepartmentsNotFlow.keys

        val departments = mutableSetOf<DepartmentEntity>()

        trackedTeachersDepartmentsNotFlow.values.forEach {
            departments.addAll(it)
        }

        val teacherNameForParsing = {
                teacher: TeacherEntity ->
            getInitials(teacher.name, teacher.surname, teacher.patronymic)
        }

        val teacherShortcuts =
            teachers.associate { Pair(it.fio, teacherNameForParsing(it)) }

        val actualSchedule = parser.getActualSchedule(
            teachers = teachers.map { teacherNameForParsing(it) },
            departments = departments.map { it.name },
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(14),
        )

        val schedule = teachers.map {
            Pair(it.teacherId,
                actualSchedule.lessons.getOrDefault(teacherShortcuts[it.fio], emptyList()))
        }

        val lessonEntities = schedule.map {
            fromLessonParsingModelsToEntities(it.second, departments.toList(), it.first)
        }
            .flatten().filter {
                val teacher = teachers.find {
                        teacher -> teacher.teacherId == it.teacherId }

                if (teacher != null) {
                    val trackingDepartments = trackedTeachersDepartmentsNotFlow[teacher]

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

    /**
     * Получить и сохранить расписание для всех отлеживаемых преподавателей.
     * @param teacherId идентификатор преподавателя.
     * @param departmentId идентификатор кафедры.
     */
    fun saveSchedule(teacherId: String, departmentId: String) {
        val teacher = scheduleTrackerDao.getTeacher(teacherId)
        val department = scheduleTrackerDao.getDepartment(departmentId)

        if (teacher == null || department == null) {
            return
        }

        val teacherInitials = getInitials(teacher.name, teacher.surname, teacher.patronymic)

        val actualSchedule = parser.getActualSchedule(
            teachers = listOf(teacherInitials),
            departments = listOf(department.name),
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(14),
        )

        val lessonEntities = actualSchedule.lessons.map {
            it.value.map {
                lesson -> fromLessonParsingModelsToEntity(
                    lessonParsingModel = lesson,
                    teacherId = teacher.teacherId,
                    departmentId = department.departmentId)
            }
        }.flatten()

        lessonEntities.forEach {
            scheduleTrackerDao.insert(it)
        }
    }

    /**
     * Вставить занятие.
     */
    fun insertLesson(lessonEntity: LessonEntity) {
        scheduleTrackerDao.insert(lessonEntity)
    }

    fun getLessonsByDatetime(teacherId: String, departmentId: String, date: String, time: String): LessonEntity {
        return scheduleTrackerDao.getLesson(teacherId, departmentId, date, time)
    }
}