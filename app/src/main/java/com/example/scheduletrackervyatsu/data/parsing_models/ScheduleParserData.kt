package com.example.scheduletrackervyatsu.data.parsing_models

import java.time.LocalDateTime

/**
 * Данные парсинга расписания.
 * @param lessons список пар.
 * @param modifiedOn дата и время проверка расписания.
 */
data class ScheduleParserData(
    val lessons: Map<String, List<LessonParsingModel>>,
    val modifiedOn: LocalDateTime
)