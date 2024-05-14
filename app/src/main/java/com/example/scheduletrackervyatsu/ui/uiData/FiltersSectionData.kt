package com.example.scheduletrackervyatsu.ui.uiData

import com.example.scheduletrackervyatsu.DateIntervals
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity

data class FiltersSectionData(
    val department: DepartmentEntity?,
    val teacher: TeacherEntity?,
    val datetimeInterval: DateIntervals,
    val departments: List<DepartmentEntity>,
    val teachers: List<TeacherEntity>,
    val datetimeIntervals: Map<DateIntervals, String>,
    val onSelectedDepartmentChange: (String) -> Unit,
    val onSelectedTeacherChange: (String) -> Unit,
    val onSelectedDateTimeIntervalChange: (String) -> Unit,
    val trackingTeachers: List<TeacherEntity>
)
