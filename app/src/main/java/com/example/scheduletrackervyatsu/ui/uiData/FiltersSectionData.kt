package com.example.scheduletrackervyatsu.ui.uiData

import com.example.scheduletrackervyatsu.data.entities.TeacherEntity

/**
 * Данные для секции фильтров.
 * @param teacher выбранный преподаватель.
 * @param onSelectedTeacherChange обработчик смены преподавателя.
 * @param trackingTeachers отслеживаемые преподаватели.
 */
data class FiltersSectionData(
    val teacher: TeacherEntity?,
    val onSelectedTeacherChange: (String) -> Unit,
    val trackingTeachers: List<TeacherEntity>
)
