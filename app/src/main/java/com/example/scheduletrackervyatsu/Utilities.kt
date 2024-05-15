package com.example.scheduletrackervyatsu

import com.example.scheduletrackervyatsu.data.entities.LessonStatusEntity

const val DATABASE_NAME = "scheduleTracker.db"


val LESSON_STATUSES = listOf<LessonStatusEntity>(
    LessonStatusEntity(lessonStatusId = 0, "Пустое"),
    LessonStatusEntity(lessonStatusId = 1, "Новое"),
    LessonStatusEntity(lessonStatusId = 2, "Изменены дынные"),
    LessonStatusEntity(lessonStatusId = 3, "Удалённое"),
    LessonStatusEntity(lessonStatusId = 4, "Изменена аудитория"),
)


enum class DateIntervals {
    Week,
    TwoWeeks,
    CurrentWeek,
    CurrentAndNextWeeks
}

val DateIntervalsStringValues = mapOf(
    DateIntervals.Week to "1 неделя",
    DateIntervals.TwoWeeks to "2 недели",
    DateIntervals.CurrentWeek to "Текущая неделя",
    DateIntervals.CurrentAndNextWeeks to "Текущая и следующая недели",
)