package com.example.scheduletrackervyatsu.domain

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduletrackervyatsu.data.ScheduleTrackerDatabase
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application
): AndroidViewModel(application) {

    private var repository = ScheduleTrackerRepository(
        ScheduleTrackerDatabase.getDatabase(getApplication<Application>()).getScheduleTrackerDao())

    private var _departments = repository.departments
       .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var settings: Flow<List<Pair<TeacherEntity, List<DepartmentEntity>>>> = repository.settings
        .map {
            it.map {
                    entity -> Pair(entity.key, entity.value)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val departments = _departments

    var _openAddingDialogState = MutableStateFlow(AddingDepartmentDialogState())

    var openAddingDialogState = _openAddingDialogState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AddingDepartmentDialogState())

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

    fun addNewTeacher(name: String, surname: String, patronymic: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTeacher(name, surname, patronymic)
        }
    }



    fun addDepartmentForTeacher(teacherId: String, departmentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTrackingForTeacher(teacherId, departmentId)
        }
    }
}