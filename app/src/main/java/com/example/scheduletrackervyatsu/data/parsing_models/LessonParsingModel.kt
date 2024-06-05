package com.example.scheduletrackervyatsu.data.parsing_models

import java.time.LocalDate
import java.time.LocalTime

/**
 * Модель занятия для парсинга.
 * @param name название.
 * @param date дата.
 * @param time время.
 * @param department кафедра.
 * @param week учебная неделя.
 * @param dayOfWeek день недели.
 */
data class LessonParsingModel(
    val name: String,

    val date: LocalDate,

    val time: LocalTime,

    var department: String,

    val week: Boolean,

    val dayOfWeek: String
)