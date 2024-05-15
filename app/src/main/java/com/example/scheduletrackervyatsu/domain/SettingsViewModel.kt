package com.example.scheduletrackervyatsu.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduletrackervyatsu.data.ScheduleTrackerDatabase
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application
): AndroidViewModel(application) {

    /**
     * Репозиторий (источник данных).
     */
    private var repository = ScheduleTrackerRepository(
        ScheduleTrackerDatabase.getDatabase(getApplication<Application>()).getScheduleTrackerDao())

    /**
     * Список всех департаментов.
     */
    private var _departments = repository.departments
       .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Список всех департаментов.
     */
    val departments = _departments

    /**
     * Список всех преподавателей.
     */
    private var _teachers = repository.teachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Список всех преподавателей.
     */
    val teachers = _teachers

    /**
     * Состояние добавления преподавателя или кафедры.
     */
    private var _openAddingDialogState = MutableStateFlow(AddingDepartmentDialogState())

    /**
     * Состояние добавления преподавателя или кафедры.
     */
    var openAddingDialogState = _openAddingDialogState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AddingDepartmentDialogState())

    /**
     * Словарь отслеживаемых преподавателей на кафедрах.
     */
    var trackedTeachersDepartments = repository.trackedTeachersDepartments
        .map {
            it.map {
                    entity -> Pair(entity.key, entity.value)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Изменить состояние добавления преподавателя или кафедры.
     * @param isOpen открыто ли диалоговое окно.
     * @param teacherId идентификатор преподавателя.
     * @param departmentId идентификатор кафедры.
     */
    fun changeAddingDialogState(
        isOpen: Boolean = false,
        teacherId: String? = null,
        departmentId: String? = null) {
        _openAddingDialogState.value = _openAddingDialogState.value.copy(
            isOpen = isOpen,
            teacherId = teacherId,
            departmentId = departmentId
        )
    }

    /**
     * Добавление отслеживания для преподавателя на выбранной кафедре.
     * @param teacherId идентификатор преподавателя.
     * @param departmentId идентификатор кафедры.
     */
    fun addDepartmentForTeacher(teacherId: String, departmentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTrackingForTeacher(teacherId, departmentId)
            repository.saveSchedule(teacherId, departmentId)
        }
    }

    /**
     * Добавление отслеживания для преподавателя.
     * @param teacherId идентификатор преподавателя.
     */
    fun deleteTeacher(teacherId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTrackingForTeacher(teacherId)
        }
    }

    /**
     * Добавление отслеживания для преподавателя на выбранной кафедре.
     * @param teacherId идентификатор преподавателя.
     * @param departmentId идентификатор кафедры.
     */
    fun deleteDepartmentForTeacher(teacherId: String, departmentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTrackingForTeacher(teacherId, departmentId)
        }
    }
}