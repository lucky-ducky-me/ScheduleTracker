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

    fun doDailyWork(isTest: Boolean = false) : Boolean {
        val currentDateTime = LocalDateTime.now()

        val data = repository.getDayWeekAndName(LocalDate.from(currentDateTime))

        val isMorning = isFirstDayPart(currentDateTime.hour)

        var lessonToUpdate: List<LessonEntity> = emptyList()

        if (data == null) {
            Log.d("my", "Пустое расписание")
            return false
        }
        else if (isMorning && data.first && data.second == "пятница") {
            lessonToUpdate = loadNewAndCheckWithOld(isTest)
        }
        else if (!data.first && data.second == "понедельник") {
            deleteOldSchedule()
            lessonToUpdate = standardCheck(isTest)
        }
        else {
            lessonToUpdate = standardCheck(isTest)
        }

        lessonToUpdate.forEach {
            repository.insertLesson(it)
        }

        val isNeedNotification = lessonToUpdate.find {
            !it.isStatusWatched
        }

        Log.d("my", "Выполнено")

        return isNeedNotification != null
    }

    private fun deleteOldSchedule() {
        val currentDateTime = LocalDate.now()

        repository.deleteOldSchedule(currentDateTime)
    }

    private fun loadNewAndCheckWithOld(isTest: Boolean = false): List<LessonEntity> {
        val startDate = LocalDate.now().plusDays(17)
        val endDate = startDate.plusDays(1)

        val (lessonEntities, currentLessons) = getActualAndSavedLessons(
            startDate = startDate,
            endDate = endDate,
            isTest = isTest)

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


    private fun standardCheck(isTest: Boolean = false) : List<LessonEntity> {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(14)

        var (lessonEntities, currentLessons) = getActualAndSavedLessons(
            startDate = startDate, endDate = endDate, isTest = isTest)

        val dates = currentLessons.map { it.date }.toSet()

        lessonEntities = lessonEntities.filter { it.date in dates }

        return compareActualAndSavedLessons(actualLessons = lessonEntities, savedLessons = currentLessons)
    }

    private fun getActualAndSavedLessons(
        startDate: LocalDate,
        endDate: LocalDate,
        isTest: Boolean = false) : Pair<List<LessonEntity>, List<LessonEntity>> {
        val trackedTeachersDepartments = repository.getTrackedTeachersDepartments()

        val teachers = trackedTeachersDepartments.keys

        val departments = mutableSetOf<DepartmentEntity>()

        trackedTeachersDepartments.values.forEach {
            departments.addAll(it)
        }

        val teacherNameForParsing = {
                teacher: TeacherEntity -> getInitials(teacher.name, teacher.surname, teacher.patronymic)
        }

        val teacherShortcuts =
            teachers.associate { Pair(it.fio, teacherNameForParsing(it)) }

        val actualSchedule = parser.getActualSchedule(
            teachers = teachers.map { teacherNameForParsing(it) },
            departments = departments.map { it.name },
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
            fromLessonParsingModelsToEntities(it.second, departments.toList(), it.first)
        }
            .flatten().filter {
                val teacher = teachers.find {
                        teacher -> teacher.teacherId == it.teacherId }

                if (teacher != null) {
                    val trackingDepartments = trackedTeachersDepartments[teacher]

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
            }.toMutableList()

        // TODO DELETE

        if (isTest) {
            var keys = trackedTeachersDepartments.keys.toList()
            var key = keys[0]
            var first = trackedTeachersDepartments[key]
            var date = "2024-05-31"
            var time = "14:00"
            var dateForChangeOffice = "2024-05-31"
            var timeForChangeOffice = "08:20"

            var dateForChangeOfficeAndData = "2024-05-31"
            var timeForChangeOfficeAndData = "10:00"

            for (i in lessonEntities.indices) {
                val lesson = lessonEntities[i]

                if (lesson.date == date &&
                    lesson.time == time &&
                    lesson.teacherId == key.teacherId &&
                    lesson.departmentId == first!![0].departmentId
                ) {
                    var savedLesson = repository.getLessonsByDatetime(
                        key.teacherId,
                        first?.get(0)?.departmentId ?: "",
                        date,
                        time
                    )

                    lessonEntities[i] = LessonEntity(
                        lessonId = lesson.lessonId,
                        teacherId = lesson.teacherId ?: "",
                        departmentId = lesson.departmentId ?: "",
                        date = date,
                        time = time,
                        isStatusWatched = true,
                        oldData = savedLesson.oldData,
                        data = "Added",
                        oldOffice = savedLesson.oldData,
                        office = savedLesson.office,
                        dayOfWeek = savedLesson.dayOfWeek,
                        lessonStatusId = savedLesson.lessonStatusId,
                        week = savedLesson.week
                    )
                }

                if (lesson.date == dateForChangeOffice &&
                    lesson.time == timeForChangeOffice &&
                    lesson.teacherId == key.teacherId &&
                    lesson.departmentId == first!![0].departmentId
                ) {
                    var savedLesson = repository.getLessonsByDatetime(
                        key.teacherId,
                        first?.get(0)?.departmentId ?: "",
                        dateForChangeOffice,
                        timeForChangeOffice
                    )

                    lessonEntities[i] = LessonEntity(
                        lessonId = lesson.lessonId,
                        teacherId = lesson.teacherId ?: "",
                        departmentId = lesson.departmentId ?: "",
                        date = dateForChangeOffice,
                        time = timeForChangeOffice,
                        isStatusWatched = true,
                        oldData = savedLesson.oldData,
                        data = savedLesson.data,
                        oldOffice = savedLesson.oldData,
                        office = "13-666",
                        dayOfWeek = savedLesson.dayOfWeek,
                        lessonStatusId = savedLesson.lessonStatusId,
                        week = savedLesson.week
                    )
                }

                if (lesson.date == dateForChangeOfficeAndData &&
                    lesson.time == timeForChangeOfficeAndData &&
                    lesson.teacherId == key.teacherId &&
                    lesson.departmentId == first!![0].departmentId
                ) {
                    var savedLesson = repository.getLessonsByDatetime(
                        key.teacherId,
                        first?.get(0)?.departmentId ?: "",
                        dateForChangeOfficeAndData,
                        timeForChangeOfficeAndData
                    )

                    lessonEntities[i] = LessonEntity(
                        lessonId = lesson.lessonId,
                        teacherId = lesson.teacherId ?: "",
                        departmentId = lesson.departmentId ?: "",
                        date = dateForChangeOfficeAndData,
                        time = timeForChangeOfficeAndData,
                        isStatusWatched = true,
                        oldData = savedLesson.oldData,
                        data = "WELCOM TO HELL!",
                        oldOffice = savedLesson.oldData,
                        office = "13-666",
                        dayOfWeek = savedLesson.dayOfWeek,
                        lessonStatusId = savedLesson.lessonStatusId,
                        week = savedLesson.week
                    )
                }
            }

            CHANGE_FOR_DELETED_TEST(trackedTeachersDepartments)

        }
        // TODO DELETE

        val currentDateTime = LocalDateTime.now()

        val currentLessons = repository.getAllLessons().filter{
            it.date >= LocalDate.from(currentDateTime).toString().split("T")[0]}

        return Pair(lessonEntities.toList(), currentLessons)
    }

    private fun CHANGE_FOR_DELETED_TEST(
        trackedTeachersDepartments: Map<TeacherEntity, List<DepartmentEntity>>) {
        var keys = trackedTeachersDepartments.keys.toList()
        var key = keys[0]
        var first = trackedTeachersDepartments[key]

        var date = "2024-05-31"
        var time = "11:45"

        var lesson = repository.getLessonsByDatetime(
            key.teacherId,
            first?.get(0)?.departmentId ?: "",
            date,
            time)

        repository.insertLesson(LessonEntity(
            lessonId = lesson.lessonId,
            teacherId = lesson.teacherId ?: "",
            departmentId = lesson.departmentId ?: "",
            date = date,
            time = time,
            isStatusWatched = true,
            oldData = lesson.oldData,
            data = "TestLesson",
            oldOffice = lesson.oldOffice,
            office = "16-414",
            dayOfWeek = lesson.dayOfWeek,
            lessonStatusId = lesson.lessonStatusId,
            week = lesson.week))
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
                office = savedLesson.office,
                dayOfWeek = savedLesson.dayOfWeek,
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
                dayOfWeek = savedLesson.dayOfWeek,
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
                oldOffice = savedLesson.oldOffice,
                office = savedLesson.office,
                dayOfWeek = savedLesson.dayOfWeek,
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
                dayOfWeek = savedLesson.dayOfWeek,
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
                dayOfWeek = savedLesson.dayOfWeek,
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
                dayOfWeek = actualLesson.dayOfWeek,
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
                dayOfWeek = actualLesson.dayOfWeek,
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
                oldOffice = savedLesson.oldOffice,
                office = actualLesson.office,
                dayOfWeek = actualLesson.dayOfWeek,
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
                dayOfWeek = actualLesson.dayOfWeek,
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
                dayOfWeek = actualLesson.dayOfWeek,
                lessonStatusId = 6,
                week = actualLesson.week
            )
        }

        return newLessonEntity
    }
}