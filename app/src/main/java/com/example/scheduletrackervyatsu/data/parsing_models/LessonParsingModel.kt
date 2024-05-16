package com.example.scheduletrackervyatsu.data.parsing_models

import java.time.LocalDate
import java.time.LocalTime

data class LessonParsingModel(
    val name: String,
    val date: LocalDate,
    val time: LocalTime,
    var department: String,
    val week: Boolean,
    val dayOfWeek: String
)