package com.example.scheduletrackervyatsu.domain

/**
 * Состояние при добавлении кафедры для преподавателя.
 * @param isOpen открыто ли диалоговое окно.
 * @param teacherId Id преподавателя.
 * @param departmentId Id кафедры.
 */
data class AddingDepartmentDialogState(
    var isOpen: Boolean = false,
    var teacherId: String? = null,
    var departmentId: String? = null,
)