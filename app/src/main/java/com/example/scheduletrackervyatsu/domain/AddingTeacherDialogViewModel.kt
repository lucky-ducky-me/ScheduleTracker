package com.example.scheduletrackervyatsu.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddingTeacherDialogViewModel(
    application: Application,
): AndroidViewModel(application) {
    /**
     * Выбранный департамент.
     */
    private var _department = MutableStateFlow<DepartmentEntity?>(null)

    /**
     * Выбранный департамент.
     */
    val department
        get() = _department.asStateFlow()

    fun onSelectTeacherClick(departmentEntity: DepartmentEntity) {
        _department.update { departmentEntity }
    }
}