package com.example.scheduletrackervyatsu

import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.LessonStatusEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import java.time.LocalDate

const val DATABASE_NAME = "scheduleTracker.db"

enum class LessonStatusKey {
    NEW,
    ADDED,
    CHANGED_DATA,
    DELETED,
    CHANGED_OFFICE,
    CHANGED_DATA_AND_OFFICE
}

val LESSON_STATUSES = listOf<LessonStatusEntity>(
    LessonStatusEntity(lessonStatusId = 1, "Первая инициализация"),
    LessonStatusEntity(lessonStatusId = 2, "Добавленное"),
    LessonStatusEntity(lessonStatusId = 3, "Изменены дынные"),
    LessonStatusEntity(lessonStatusId = 4, "Удалённое"),
    LessonStatusEntity(lessonStatusId = 5, "Изменена аудитория"),
    LessonStatusEntity(lessonStatusId = 6, "Изменены дынные и аудитория"),
)

val UNIQUE_LESSONS = listOf(
    "Консультация",
    "Экзамен",
    "Зачет",
    "Зачет по практике",
    "Кандидатский экзамен",
    "Защита ВКР",
    "Защита курсовых работ",
)

val VYATSU_URL = "https://www.vyatsu.ru/"
val SCHEDULE_URL = "studentu-1/spravochnaya-informatsiya/teacher.html"
val TEACHER_API_URL = "php/sotr_prepod_list/ajax_prepod.php"

const val RUSSIAN_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшцыэюя"

fun getDataForTest(
    trackedTeachersDepartments: Map<TeacherEntity, List<DepartmentEntity>>,
    actualLessons: List<LessonEntity>,
    testDate: LocalDate = LocalDate.now(),
    repository: ScheduleTrackerRepository
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
