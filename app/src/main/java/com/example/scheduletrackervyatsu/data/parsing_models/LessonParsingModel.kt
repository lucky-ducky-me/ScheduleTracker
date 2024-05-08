package com.example.scheduletrackervyatsu.data.parsing_models

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.LocalDate

data class LessonParsingModel(
    val name: String,
    val date: LocalDate,
    val time: LocalTime,
    var department: String,
)
