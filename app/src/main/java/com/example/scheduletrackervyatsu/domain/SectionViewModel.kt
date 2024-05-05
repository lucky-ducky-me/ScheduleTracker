package com.example.scheduletrackervyatsu.domain

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduletrackervyatsu.data.ScheduleTrackerDatabase
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SectionViewModel(
    application: Application,
): AndroidViewModel(application) {
    private var repository = ScheduleTrackerRepository(
            ScheduleTrackerDatabase.getDatabase(getApplication<Application>()).getScheduleTrackerDao())

    private var _departments = repository.departments
        .map {
            it.map {
                    entity -> entity.name ?: ""
            }
        }.filter {
            it.isNotEmpty()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var teachers = mutableListOf<String>(
        "Teacher teacher 1",
        "Teacher teacher 2",
        "Teacher teacher 3",
        "Teacher teacher 4",
        "Teacher teacher 5",
        "Teacher teacher 6",
        "Teacher teacher 7")
        .toMutableStateList()

    var dateIntervals = arrayOf<String>(
        "Текущая неделя",
        "Текущая и следующие недели",
        "1 неделя",
        "2 недели")

    private var _department = mutableStateOf("")

    private var _teacher = mutableStateOf(teachers[0])

    private var _dateIntervals = mutableStateOf(dateIntervals[0])

    val department
        get() = _department

    val departments = _departments

    val teacher
        get() = _teacher

    init {
//        viewModelScope.launch {
//            var list = repository.departments.map {
//                it.map {
//                        entity -> entity.name ?: "null"
//                }
//            }
//
//            list.collect {
//                entity -> _departments.value = entity
//        }
//
//
//        }
    }

    fun changeCurrentDepartment(department: String) {
        _department.value = department
        Log.d("value", department)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAll()
        }
    }

    fun changeCurrentTeacher(teacher: String) {
        _teacher.value = teacher
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAll()
        }
    }

    fun changeDateInterval(dateInterval: String) {
        _dateIntervals.value = dateInterval
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAll()
        }
    }
}