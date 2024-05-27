package com.example.scheduletrackervyatsu.domain

import android.util.Log
import com.example.scheduletrackervyatsu.LessonStatusKey
import com.example.scheduletrackervyatsu.UNIQUE_LESSONS
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import com.example.scheduletrackervyatsu.data.dao.VyatsuParser
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.helpers.fromLessonParsingModelsToEntities
import com.example.scheduletrackervyatsu.data.helpers.getInitials
import java.time.LocalDate
import java.time.LocalDateTime

class DailyWorker(private val repository: ScheduleTrackerRepository) {

    private val parser = VyatsuParser()

    /**
     * Выполнение ежедневной работы.
     */
    fun doDailyWork(
        isTest: Boolean = false,
        testDate: LocalDate = LocalDate.now()) : Boolean {

        val currentDateTime = LocalDateTime.now()

        var data = repository.getDayWeekAndName(LocalDate.from(currentDateTime))

        if (data == null) {
            val dayBefore = currentDateTime.minusDays(1)

            data = repository.getDayWeekAndName(dayBefore.toLocalDate())
        }

        val dayOfWeek = currentDateTime.dayOfWeek.value

        val isMorning = isFirstDayPart(currentDateTime.hour)

        val lessonToUpdate: List<LessonEntity> = if (data == null) {
            Log.d("my", "Пустое расписание")
            return false
        } else if (isMorning && data && dayOfWeek == 5) {
            loadNewAndCheckWithOld(isTest = isTest, testDate = testDate)
        } else if (!data && dayOfWeek == 1) {
            deleteOldSchedule()
            standardCheck(isTest = isTest, testDate = testDate)
        } else {
            standardCheck(isTest = isTest, testDate = testDate)
        }

        lessonToUpdate.forEach {
            repository.insertLesson(it)
        }

        val isNeedNotification = lessonToUpdate.find {
            !it.isStatusWatched
        }

        return isNeedNotification != null
    }

    /**
     * Удаление старого расписания.
     */
    private fun deleteOldSchedule() {
        val currentDateTime = LocalDate.now()

        repository.deleteOldSchedule(currentDateTime)
    }

    /**
     * Загрузить новое расписание и сравнить со старым.
     */
    fun loadNewAndCheckWithOld(
        isTest: Boolean = false,
        testDate: LocalDate = LocalDate.now()): List<LessonEntity> {

        val now = LocalDate.now()
        val dayOfWeek = now.dayOfWeek.value

        val twoWeeksDays = 14
        val difference = if (dayOfWeek == 0 || dayOfWeek == 5 || dayOfWeek == 6) 3 else 0


        val startDate = LocalDate.now().plusDays((twoWeeksDays + difference).toLong())

        val (lessonEntities, currentLessons) = getActualAndSavedLessons(
            startDate = startDate,
            endDate = startDate,
            isTest = isTest,
            testDate = testDate
        )

        var resultEntities = lessonEntities.filter {
            !currentLessons.contains(it)
        }.toMutableList()

        val savedLessons = repository.getAllLessons()

        if (lessonEntities.isEmpty()) {
            return emptyList()
        }

        for (i in resultEntities.indices) {
            val isUniqueLesson = {
                lesson: String ->
                var result = false
                UNIQUE_LESSONS.forEach {
                    result = result || lesson.contains(it)
                }
                result
            }

            val sameLessonTwoWeeksAgo = savedLessons.find {
                    savedLesson ->
                LocalDate.parse(resultEntities[i].date).minusDays(14) ==
                        LocalDate.parse(savedLesson.date) &&
                        resultEntities[i].time == savedLesson.time &&
                        resultEntities[i].departmentId == savedLesson.departmentId &&
                        resultEntities[i].teacherId == savedLesson.teacherId &&
                        savedLesson.lessonStatusId == 1
                        && !isUniqueLesson(savedLesson.data)
                        &&!isUniqueLesson(resultEntities[i].data)
            }

            if (sameLessonTwoWeeksAgo != null) {
                val newLesson = createNewLesson(resultEntities[i], sameLessonTwoWeeksAgo)

                resultEntities[i] = newLesson
            }
        }

        return resultEntities
    }


    /**
     * Стандартная проверка.
     */
    fun standardCheck(
        isTest: Boolean = false,
        testDate: LocalDate = LocalDate.now()) : List<LessonEntity> {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(14)

        var (lessonEntities, currentLessons) = getActualAndSavedLessons(
            startDate = startDate, endDate = endDate, isTest = isTest, testDate = testDate)

        val dates = currentLessons.map { it.date }.toSet()

        lessonEntities = lessonEntities.filter { it.date in dates }

        return compareActualAndSavedLessons(actualLessons = lessonEntities,
            savedLessons = currentLessons)
    }

    /**
     * Получение актуального и сохранённого расписания.
     */
    private fun getActualAndSavedLessons(
        startDate: LocalDate,
        endDate: LocalDate,
        isTest: Boolean = false,
        testDate: LocalDate = LocalDate.now()
    ) : Pair<List<LessonEntity>, List<LessonEntity>> {

        val trackedTeachersDepartments = repository.getTrackedTeachersDepartments()

        val teachers = trackedTeachersDepartments.keys

        val trackingDepartments = mutableSetOf<DepartmentEntity>()

        trackedTeachersDepartments.values.forEach {
            trackingDepartments.addAll(it)
        }

        val teacherNameForParsing = {
            teacher: TeacherEntity -> getInitials(
                teacher.name,
                teacher.surname,
                teacher.patronymic)
        }

        val teacherShortcuts =
            teachers.associate { Pair(it.fio, teacherNameForParsing(it)) }

        val actualSchedule = parser.getActualSchedule(
            teachers = teachers.map { teacherNameForParsing(it) },
            departments = trackingDepartments.map { it.name },
            startDate = startDate,
            endDate = endDate,
        )

        if (actualSchedule.lessons.isEmpty()) {
            return Pair(listOf(), listOf())
        }

        val schedule = teachers.map {
            Pair(it.teacherId,
                actualSchedule.lessons.getOrDefault(teacherShortcuts[it.fio], emptyList()))
        }

        var lessonEntities = schedule.map {
            fromLessonParsingModelsToEntities(it.second, trackingDepartments.toList(), it.first)
        }
            .flatten().filter {
                val teacher = teachers.find {
                        teacher -> teacher.teacherId == it.teacherId }

                if (teacher != null) {
                    val trackingDepartmentsValue = trackedTeachersDepartments[teacher]

                    if (trackingDepartmentsValue != null) {
                        trackingDepartmentsValue.find {
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

        val currentDateTime = LocalDateTime.now()

        val currentLessons = repository.getAllLessons().filter{
            it.date >= LocalDate.from(currentDateTime).toString().split("T")[0]}

        // Тестовая секция.
        if (isTest) {
            lessonEntities = getDataForTest(
                trackedTeachersDepartments = trackedTeachersDepartments,
                actualLessons = lessonEntities,
                testDate = testDate)
        }

        return Pair(lessonEntities, currentLessons)
    }

    private fun getDataForTest(
        trackedTeachersDepartments: Map<TeacherEntity, List<DepartmentEntity>>,
        actualLessons: List<LessonEntity>,
        testDate: LocalDate = LocalDate.now()
    ): List<LessonEntity> {

        val keys = trackedTeachersDepartments.keys.toList()
        val teacherEntity = keys[0]
        val trackedDepartment = trackedTeachersDepartments[teacherEntity]?.first()

        val resultActualLessons = actualLessons.toMutableList()

        val changingDayLessons = resultActualLessons.filter {
            it.date == testDate.toString()
        }

        val lessonForChangeData = changingDayLessons.find { it.data.isNotEmpty() }
        val lessonForDelete = changingDayLessons.find {
            it.data.isNotEmpty() &&
                    it.lessonId != (lessonForChangeData?.lessonId ?: "")
        }
        val lessonForAdd = changingDayLessons.find { it.data.isEmpty() }

        if (lessonForChangeData != null) {
            val savedLesson = repository.getLessonsByDatetime(
                teacherEntity.teacherId,
                trackedDepartment?.departmentId ?: "",
                lessonForChangeData.date,
                lessonForChangeData.time
            )

            val index = resultActualLessons.indexOf(lessonForChangeData)

            if (index != - 1) {
                resultActualLessons[index] = LessonEntity(
                    lessonId = lessonForChangeData.lessonId,
                    teacherId = lessonForChangeData.teacherId ?: "",
                    departmentId = lessonForChangeData.departmentId ?: "",
                    date = lessonForChangeData.date,
                    time = lessonForChangeData.time,
                    isStatusWatched = true,
                    oldData = null,
                    data = "Изменено или добавлено тестовое занятие...",
                    oldOffice = null,
                    office = "13-666",
                    lessonStatusId = savedLesson.lessonStatusId,
                    week = savedLesson.week
                )
            }
        }

        if (lessonForDelete != null) {
            val savedLesson = repository.getLessonsByDatetime(
                teacherEntity.teacherId,
                trackedDepartment?.departmentId ?: "",
                lessonForDelete.date,
                lessonForDelete.time
            )

            val index = resultActualLessons.indexOf(lessonForDelete)

            if (index != - 1) {
                resultActualLessons[index] = LessonEntity(
                    lessonId = lessonForDelete.lessonId,
                    teacherId = lessonForDelete.teacherId ?: "",
                    departmentId = lessonForDelete.departmentId ?: "",
                    date = lessonForDelete.date,
                    time = lessonForDelete.time,
                    isStatusWatched = true,
                    oldData = savedLesson.data,
                    data = "",
                    oldOffice = savedLesson.office,
                    office = "",
                    lessonStatusId = savedLesson.lessonStatusId,
                    week = savedLesson.week
                )
            }
        }

        if (lessonForAdd != null) {
            val savedLesson = repository.getLessonsByDatetime(
                teacherEntity.teacherId,
                trackedDepartment?.departmentId ?: "",
                lessonForAdd.date,
                lessonForAdd.time
            )

            val index = resultActualLessons.indexOf(lessonForAdd)

            if (index != - 1) {
                resultActualLessons[index] = LessonEntity(
                    lessonId = lessonForAdd.lessonId,
                    teacherId = lessonForAdd.teacherId ?: "",
                    departmentId = lessonForAdd.departmentId ?: "",
                    date = lessonForAdd.date,
                    time = lessonForAdd.time,
                    isStatusWatched = true,
                    oldData = savedLesson.data,
                    data = "Добавлено тестовое занятие...",
                    oldOffice = savedLesson.office,
                    office = "13-666",
                    lessonStatusId = savedLesson.lessonStatusId,
                    week = savedLesson.week
                )
            }
        }

        return resultActualLessons
    }

    private fun compareActualAndSavedLessons(
        actualLessons: List<LessonEntity>,
        savedLessons: List<LessonEntity>): List<LessonEntity> {

        val lessonsToUpdate: MutableList<LessonEntity> = mutableListOf()

        for (i in savedLessons.indices) {
            val savedLesson = savedLessons[i]

            val actualLesson = actualLessons.find {
                    actualLesson ->
                    actualLesson.date == savedLesson.date &&
                            actualLesson.time == savedLesson.time &&
                            actualLesson.departmentId == savedLesson.departmentId &&
                            actualLesson.teacherId == savedLesson.teacherId
            }

            if (actualLesson != null) {
                var newLessonEntity: LessonEntity? = getNewLesson(actualLesson, savedLesson)

                if (newLessonEntity != null) {
                    lessonsToUpdate.add(newLessonEntity)
                }
            }
        }

        return lessonsToUpdate
    }

    private fun compareActualAndSavedLesson(
        actualLesson: LessonEntity,
        savedLesson: LessonEntity) : LessonStatusKey {
        if (actualLesson.data.isEmpty() && savedLesson.data.isNotEmpty()) {
            return LessonStatusKey.DELETED
        }

        if (actualLesson.data.isNotEmpty() && savedLesson.data.isEmpty()) {
            return LessonStatusKey.ADDED
        }

        if (actualLesson.data != savedLesson.data && actualLesson.office != savedLesson.office) {
            return LessonStatusKey.CHANGED_DATA_AND_OFFICE
        }


        if (actualLesson.data != savedLesson.data) {
            return LessonStatusKey.CHANGED_DATA
        }

        if (actualLesson.office != savedLesson.office) {
            return LessonStatusKey.CHANGED_OFFICE
        }

        return LessonStatusKey.NEW
    }

    private fun isFirstDayPart(hour: Int): Boolean {
        return hour in 1..11
    }

    private fun getNewLesson(
        actualLesson: LessonEntity,
        savedLesson: LessonEntity)
    : LessonEntity? {
        var newLessonEntity: LessonEntity? = null

        when (compareActualAndSavedLesson(actualLesson, savedLesson)) {
            LessonStatusKey.NEW -> newLessonEntity = null
            LessonStatusKey.ADDED -> newLessonEntity = LessonEntity(
                lessonId = savedLesson.lessonId,
                teacherId = savedLesson.teacherId,
                departmentId = savedLesson.departmentId,
                date = savedLesson.date,
                time = savedLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = actualLesson.data,
                oldOffice = savedLesson.oldOffice,
                office = actualLesson.office,
                lessonStatusId = 2,
                week = savedLesson.week
            )
            LessonStatusKey.CHANGED_DATA -> newLessonEntity = LessonEntity(
                lessonId = savedLesson.lessonId,
                teacherId = savedLesson.teacherId,
                departmentId = savedLesson.departmentId,
                date = savedLesson.date,
                time = savedLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = actualLesson.data,
                oldOffice = null,
                office = actualLesson.office,
                lessonStatusId = 3,
                week = savedLesson.week
            )
            LessonStatusKey.DELETED -> newLessonEntity = LessonEntity(
                lessonId = savedLesson.lessonId,
                teacherId = savedLesson.teacherId,
                departmentId = savedLesson.departmentId,
                date = savedLesson.date,
                time = savedLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = actualLesson.data,
                oldOffice = savedLesson.office,
                office = actualLesson.office,
                lessonStatusId = 4,
                week = savedLesson.week
            )
            LessonStatusKey.CHANGED_OFFICE -> newLessonEntity = LessonEntity(
                lessonId = savedLesson.lessonId,
                teacherId = savedLesson.teacherId,
                departmentId = savedLesson.departmentId,
                date = savedLesson.date,
                time = savedLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = savedLesson.data,
                oldOffice = savedLesson.office,
                office = actualLesson.office,
                lessonStatusId = 5,
                week = savedLesson.week
            )
            LessonStatusKey.CHANGED_DATA_AND_OFFICE -> newLessonEntity = LessonEntity(
                lessonId = savedLesson.lessonId,
                teacherId = savedLesson.teacherId,
                departmentId = savedLesson.departmentId,
                date = savedLesson.date,
                time = savedLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = actualLesson.data,
                oldOffice = savedLesson.office,
                office = actualLesson.office,
                lessonStatusId = 6,
                week = savedLesson.week
            )
        }

        return newLessonEntity
    }

    private fun createNewLesson(
        actualLesson: LessonEntity,
        savedLesson: LessonEntity)
            : LessonEntity {
        lateinit var newLessonEntity: LessonEntity

        when (compareActualAndSavedLesson(actualLesson, savedLesson)) {
            LessonStatusKey.NEW -> newLessonEntity = actualLesson
            LessonStatusKey.ADDED -> newLessonEntity = LessonEntity(
                lessonId = actualLesson.lessonId,
                teacherId = actualLesson.teacherId,
                departmentId = actualLesson.departmentId,
                date = actualLesson.date,
                time = actualLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = actualLesson.data,
                oldOffice = savedLesson.oldOffice,
                office = actualLesson.office,
                lessonStatusId = 2,
                week = actualLesson.week
            )
            LessonStatusKey.CHANGED_DATA -> newLessonEntity = LessonEntity(
                lessonId = actualLesson.lessonId,
                teacherId = actualLesson.teacherId,
                departmentId = actualLesson.departmentId,
                date = actualLesson.date,
                time = actualLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = actualLesson.data,
                oldOffice = null,
                office = actualLesson.office,
                lessonStatusId = 3,
                week = actualLesson.week
            )
            LessonStatusKey.DELETED -> newLessonEntity = LessonEntity(
                lessonId = actualLesson.lessonId,
                teacherId = actualLesson.teacherId,
                departmentId = actualLesson.departmentId,
                date = actualLesson.date,
                time = actualLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = actualLesson.data,
                oldOffice = savedLesson.office,
                office = actualLesson.office,
                lessonStatusId = 4,
                week = actualLesson.week
            )
            LessonStatusKey.CHANGED_OFFICE -> newLessonEntity = LessonEntity(
                lessonId = actualLesson.lessonId,
                teacherId = actualLesson.teacherId,
                departmentId = actualLesson.departmentId,
                date = actualLesson.date,
                time = actualLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = actualLesson.data,
                oldOffice = savedLesson.oldOffice,
                office = actualLesson.office,
                lessonStatusId = 5,
                week = actualLesson.week
            )
            LessonStatusKey.CHANGED_DATA_AND_OFFICE -> newLessonEntity = LessonEntity(
                lessonId = actualLesson.lessonId,
                teacherId = actualLesson.teacherId,
                departmentId = actualLesson.departmentId,
                date = actualLesson.date,
                time = actualLesson.time,
                isStatusWatched = false,
                oldData = savedLesson.data,
                data = actualLesson.data,
                oldOffice = savedLesson.oldOffice,
                office = actualLesson.office,
                lessonStatusId = 6,
                week = actualLesson.week
            )
        }

        return newLessonEntity
    }
}