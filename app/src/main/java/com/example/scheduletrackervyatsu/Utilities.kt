package com.example.scheduletrackervyatsu

import com.example.scheduletrackervyatsu.data.entities.LessonStatusEntity

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
