package com.example.scheduletrackervyatsu.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduletrackervyatsu.data.ScheduleTrackerDatabase
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

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
     * Занятия.
     */
    private var _lessons = MutableStateFlow<List<LessonEntity>>(emptyList())

    val lessons
        get() = _lessons

    /**
     * По неделям.
     */
    private var _lessonsByWeeks = MutableStateFlow<List<Pair<Int, List<LessonEntity>>>>(emptyList())

    val lessonsByWeeks
        get() = _lessonsByWeeks



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

    /**
     * Изменить выбранный департамент.
     */
    fun changeCurrentDepartment(departmentId: String) {
        _department.update {
            _trackingTeacherDepartments.value.find { it.departmentId == departmentId }
                ?: _department.value
        }
    }

    /**
     * Изменить выбранного преподавателя.
     */
    fun changeCurrentTeacher(teacherId: String) {
        _teacher.update {
            _trackingTeachers.value.find {it.teacherId == teacherId}
                ?: _teacher.value
        }
    }

    /**
     * Получить занятия.
     */
    fun getLessons() {
        viewModelScope.launch(Dispatchers.IO) {
            _lessons.update {
                repository.getLessons(
                    teacher.value?.teacherId ?: "",
                    department.value?.departmentId ?: ""
                )
            }

            setLessonsByWeeks()
        }
    }

    fun watchLessonStatus(lessonId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeLessonStatusVisibility(lessonId, true)
        }
    }

    /**
     * Распределить занятия по неделям.
     */
    private fun setLessonsByWeeks() {
        val map = mutableMapOf<String, List<LessonEntity>>()

        _lessons.value.forEach {
                lesson ->
            val date = lesson.date

            if (!map.containsKey(date)) {
                map[date] = emptyList()
            }

            map[date] = map[date]!!.plus(listOf(lesson))
        }

        val lessonsByDays = map.toList().sortedWith(
            compareBy { it.first }
        )

        val lessonsByWeeksMap = mutableMapOf<Int, List<LessonEntity>>()

        var cntDay = 0
        val dayCount = 6

        lessonsByDays.forEach {
            if (it.second[0].dayOfWeek.lowercase(Locale.ROOT) == "воскресенье") {
                cntDay--
            }

            val week = cntDay / (dayCount)

            if (!lessonsByWeeksMap.containsKey(week)) {
                lessonsByWeeksMap[week] = emptyList()
            }

            lessonsByWeeksMap[week] =  lessonsByWeeksMap[week]!!.plus(it.second)

            cntDay++
        }

        _lessonsByWeeks.update {
            lessonsByWeeksMap.toList().sortedWith(
                compareBy { it.first }
            )
        }
    }

    fun testButton() {
        viewModelScope.launch(Dispatchers.IO) {
            val worker = DailyWorker(repository)

            worker.doDailyWork(true)
        }
    }
}