package com.example.scheduletrackervyatsu.domain

data class AddingDepartmentDialogState(
    var isOpen: Boolean = false,
    var teacherId: String? = null,
    var departmentId: String? = null,
)