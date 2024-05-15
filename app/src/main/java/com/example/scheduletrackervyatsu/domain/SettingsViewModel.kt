package com.example.scheduletrackervyatsu.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduletrackervyatsu.data.ScheduleTrackerDatabase
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import com.example.scheduletrackervyatsu.data.dao.VyatsuParser
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    private var _teachers = repository.teachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private var _openAddingDialogState = MutableStateFlow(AddingDepartmentDialogState())

    val departments = _departments

    val teachers = _teachers


    var openAddingDialogState = _openAddingDialogState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AddingDepartmentDialogState())

    var settings = repository.trackedTeachersDepartments
        .map {
            it.map {
                    entity -> Pair(entity.key, entity.value)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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

    fun addDepartmentForTeacher(teacherId: String, departmentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTrackingForTeacher(teacherId, departmentId)
        }
    }

    fun deleteTeacher(teacherId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //todo исправить на удаление отслеживания для всех кафедр :) а то спарсил а потом удалил бз БД гений
            //repository.deleteTeacher(teacherId)
        }
    }

    fun deleteDepartmentForTeacher(teacherId: String, departmentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTrackingForTeacher(teacherId, departmentId)
        }
    }
}