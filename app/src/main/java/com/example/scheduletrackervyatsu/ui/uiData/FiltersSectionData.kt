package com.example.scheduletrackervyatsu.ui.uiData

import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity

data class FiltersSectionData(
    val department: DepartmentEntity?,
    val teacher: TeacherEntity?,
    val datetimeInterval: String,
    val departments: List<DepartmentEntity>,
    val teachers: List<TeacherEntity>,
    val datetimeIntervals: List<String>,
    val onSelectedDepartmentChange: (String) -> Unit,
    val onSelectedTeacherChange: (String) -> Unit,
    val onSelectedDateTimeIntervalChange: (String) -> Unit,
    val trackingTeachers: List<TeacherEntity>
)
