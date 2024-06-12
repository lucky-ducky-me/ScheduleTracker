package com.example.scheduletrackervyatsu.data

import com.example.scheduletrackervyatsu.data.dao.ScheduleTrackerDao
import com.example.scheduletrackervyatsu.data.dao.VyatsuParser
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.Logs
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeachersDepartmentCrossRef
import com.example.scheduletrackervyatsu.data.helpers.fromLessonParsingModelsToEntity
import com.example.scheduletrackervyatsu.data.helpers.getInitials
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Репозиторий приложения.
 */
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
        val tracking = scheduleTrackerDao.getTeachersDepartmentCrossRef(
            teacherId = teacherId,
            departmentId = departmentId)

        if (tracking != null) {
            return
        }

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
        scheduleTrackerDao.deleteTeacherData(teacherId = teacherId, departmentId = departmentId)
    }

    /**
     * Удалить отслеживание для преподавателя.
     * @param teacherId идентификатор преподавателя.
     */
    fun deleteTrackingForTeacher(teacherId: String) {
        scheduleTrackerDao.deleteTeacherData(teacherId = teacherId)
    }

    /**
     * Получить занятия.
     * @param teacherId идентификатор преподавателя.
     * @return список занятий.
     */
    fun getLessons(teacherId: String): List<LessonEntity>{
        return scheduleTrackerDao.getLessons(teacherId)
    }

    /**
     * Получить занятия.
     * @param teacherId идентификатор преподавателя.
     * @return список занятий.
     */
    fun getLessonsFlow(teacherId: String): Flow<List<LessonEntity>>{
        return scheduleTrackerDao.getLessonsFlow(teacherId)
    }

    /**
     * Получить занятия.
     * @return список занятий.
     */
    fun getAllLessons(): List<LessonEntity> {
        return scheduleTrackerDao.getAllLessons()
    }

    /**
     * Получить всех преподавателей и их кафедры.
     */
    fun getTrackedTeachersDepartments(): Map<TeacherEntity, List<DepartmentEntity>> {
        return scheduleTrackerDao.getTrackedTeachersDepartmentsNowFlow()
    }

    //region Функции для DailyReceiver

    /**
     * Получить учебную неделю по дню.
     * @param day день недели.
     */
    fun getWeek(day: LocalDate): Boolean? {
        var ans = scheduleTrackerDao.getLessonsByDay(day.toString())

        if (ans != null && ans.isNotEmpty()) {
            return ans[0].week
        }

        val prevDay = day.minusDays(14)

        ans = scheduleTrackerDao.getLessonsByDay(prevDay.toString())

        if (ans != null && ans.isNotEmpty()) {
            return ans[0].week
        }

        return null
    }


    //region Парсинг

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

    /**
     * Получить занятие по дате и времени.
     */
    fun getLessonByDatetime(teacherId: String, departmentId: String, date: String, time: String): LessonEntity {
        return scheduleTrackerDao.getLesson(teacherId, departmentId, date, time)
    }

    /**
     * Изменить статус просмотра занятия.
     */
    fun changeLessonStatusVisibility(lessonId: String, isWatched: Boolean) {
        scheduleTrackerDao.changeLessonStatusVisibility(lessonId, isWatched)
    }

    /**
     * Удалить расписание до указанной даты.
     */
    fun deleteOldSchedule(currentDateTime: LocalDate?) {
        scheduleTrackerDao.deletePreviousSchedule(currentDateTime.toString())
    }

    /**
     * Получить занятие по Id.
     */
    fun getLesson(lessonId: String): Flow<LessonEntity> {
        return scheduleTrackerDao.getLesson(lessonId)
    }

    /**
     * Получить непросмотренные изменённые занятия.
     */
    fun getLessonsChangedFlow(teacherId: String): Flow<List<LessonEntity>> {
        return scheduleTrackerDao.getNotWatchLessonsFlow(teacherId)
    }

    /**
     * Получить непросмотренные изменённые занятия.
     */
    fun getLessonsChanged(teacherId: String): List<LessonEntity> {
        return scheduleTrackerDao.getNotWatchLessons(teacherId)
    }

    /**
     * Вставить лог.
     */
    fun insertLog(logValue: String) {
        scheduleTrackerDao.insert(Logs(
            text = logValue, dateTime = LocalDateTime.now().toString()))
    }

    /**
     * Изменить статус просмотра занятия.
     */
    fun changeLessonsStatusVisibility(teacherId: String, isWatched: Boolean) {
        scheduleTrackerDao.changeAllLessonsStatusVisibility(teacherId, isWatched)
    }

    /**
     * Удалить предыдущие логи.
     * @param currentDateTime дата, до которой удалять логи.
     */
    fun deleteLogs(currentDateTime: LocalDate?) {
        if (currentDateTime != null) {
            scheduleTrackerDao.deleteLogs(currentDateTime.toString())
        }
    }
}