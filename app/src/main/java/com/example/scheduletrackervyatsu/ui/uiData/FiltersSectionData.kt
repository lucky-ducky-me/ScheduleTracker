package com.example.scheduletrackervyatsu.ui.uiData

data class FiltersSectionData(
    val department: String,
    val teacher: String,
    val datetimeInterval: String,
    val departments: List<String>,
    val teachers: List<String>,
    val datetimeIntervals: List<String>,
    val onSelectedDepartmentChange: (String) -> Unit,
    val onSelectedTeacherChange: (String) -> Unit,
    val onSelectedDateTimeIntervalChange: (String) -> Unit,
)
