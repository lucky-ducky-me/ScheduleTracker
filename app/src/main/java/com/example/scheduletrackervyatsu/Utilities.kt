package com.example.scheduletrackervyatsu

const val DATABASE_NAME = "scheduleTracker.db"

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

fun <T> concatenate(vararg lists: List<T>): List<T> {
    return listOf(*lists).flatten()
}
