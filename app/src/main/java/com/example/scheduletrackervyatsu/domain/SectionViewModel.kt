package com.example.scheduletrackervyatsu.domain

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduletrackervyatsu.DateIntervals
import com.example.scheduletrackervyatsu.DateIntervalsStringValues
import com.example.scheduletrackervyatsu.data.ScheduleTrackerDatabase
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import com.example.scheduletrackervyatsu.data.dao.VyatsuParser
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class SectionViewModel(
    application: Application,
): AndroidViewModel(application) {

    /**
     * Репозиторий для получения данных.
     */
    private var repository = ScheduleTrackerRepository(
            ScheduleTrackerDatabase.getDatabase(getApplication<Application>()).getScheduleTrackerDao())

    /**
     * Отслеживаемые преподаватели.
     */
    private var _trackingTeachers = repository.trackingTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Отслеживаемые преподаватели.
     */
    val trackingTeachers = _trackingTeachers

    /**
     * Выбранный преподаватель.
     */
    private var _teacher = MutableStateFlow<TeacherEntity?>(null)

    /**
     * Выбранный преподаватель.
     */
    val teacher
        get() = _teacher.asStateFlow()

    /**
     * Департаменты выбранного преподавателя.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private var _trackingTeacherDepartments = _teacher.flatMapLatest {
        repository.getTrackingTeacherDepartments(it?.teacherId ?: "")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Департаменты выбранного преподавателя.
     */
    val trackingTeacherDepartments = _trackingTeacherDepartments

    /**
     * Выбранный департамент.
     */
    private var _department = MutableStateFlow<DepartmentEntity?>(null)

    /**
     * Выбранный департамент.
     */
    val department
        get() = _department.asStateFlow()


    /**
     * Временные промежутки.
     */
    var dateIntervals = DateIntervalsStringValues

    /**
     * Выбранный временной промежуток.
     */
    private var _dateInterval = mutableStateOf(DateIntervals.Week)

    /**
     * Выбранный временной промежуток.
     */
    val dateInterval
        get() = _dateInterval

    private var _lessons = MutableStateFlow<List<LessonEntity>>(emptyList())

    val lessons
        get() = _lessons

    /**
     * Блок инициализации.
     */
    init {
        viewModelScope.launch (Dispatchers.IO){
            _trackingTeachers.collect { teachers ->
                if (teachers.isNotEmpty() && (_teacher.value == null
                            || !teachers.contains(_teacher.value))) {
                    _teacher.update { teachers[0] }
                }
            }

        }

        viewModelScope.launch (Dispatchers.IO){
            _trackingTeacherDepartments.collect { departments ->
                if (departments.isNotEmpty()) {
                    _department.update {
                        departments[0]
                    }
                }
            }
        }
    }

    fun changeCurrentDepartment(departmentId: String) {
        _department.update {
            _trackingTeacherDepartments.value.find { it.departmentId == departmentId }
                ?: _department.value
        }
    }

    fun changeCurrentTeacher(teacherId: String) {
        _teacher.update {
            _trackingTeachers.value.find {it.teacherId == teacherId}
                ?: _teacher.value
        }
    }

    fun changeDateInterval(dateInterval: DateIntervals) {
        _dateInterval.value = dateInterval
        viewModelScope.launch (Dispatchers.IO) {
            //VyatsuParser().getTeachers()
            //repository.getAll()
        }
    }

    fun getLessons() {
        viewModelScope.launch(Dispatchers.IO) {
            _lessons.update {
                repository.getLessons(
                    teacher.value?.teacherId ?: "",
                    department.value?.departmentId ?: ""
                )
            }
        }
    }

    fun parseFullSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSchedule()
        }
    }


}