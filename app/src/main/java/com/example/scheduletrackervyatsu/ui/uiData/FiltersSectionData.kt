package com.example.scheduletrackervyatsu.ui.uiData

import com.example.scheduletrackervyatsu.data.entities.TeacherEntity

data class FiltersSectionData(
    val teacher: TeacherEntity?,
    val onSelectedTeacherChange: (String) -> Unit,
    val trackingTeachers: List<TeacherEntity>
)
