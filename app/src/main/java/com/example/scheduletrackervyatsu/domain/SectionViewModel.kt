package com.example.scheduletrackervyatsu.domain

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduletrackervyatsu.data.ScheduleTrackerDatabase
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
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
import java.time.LocalDate
import java.time.LocalDateTime

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
     * Занятия.
     */
    private var _lessons = MutableStateFlow<List<LessonEntity>>(emptyList())

    val lessons
        get() = _lessons

    private var _lessonsFlow = repository.getLessonsFlow(
        teacher.value?.teacherId ?: ""
    )

    /**
     * По неделям.
     */
    private var _lessonsByWeeks = MutableStateFlow<List<Pair<Int, List<LessonEntity>>>>(emptyList())

    val lessonsByWeeks
        get() = _lessonsByWeeks


    private var _lessonsNotWatchedFlow = repository.getLessonsChangedFlow(
        teacher.value?.teacherId ?: ""
    )

    private var _lessonsNotWatched = MutableStateFlow<List<LessonEntity>>(emptyList())

    val lessonsNotWatched
        get() = _lessonsNotWatched

    private var _currentPage = mutableIntStateOf(0)

    val currentPage = _currentPage

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
                else if (teachers.isEmpty()) {
                    _teacher.update { null}
                }
            }

        }

        viewModelScope.launch(Dispatchers.IO){
            teacher.collect {
                if (it == null) {
                    _lessons.update { emptyList() }
                    _lessonsNotWatched.update { emptyList() }
                }
                else {
                    getLessons()
                    getNowWatchLessons()
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            _lessonsFlow.collect {
                getLessons()
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            _lessonsNotWatchedFlow.collect {
                getNowWatchLessons()
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            _lessonsByWeeks.collect {
                var currentDate = LocalDate.now().toString()

                var currentWeekVal = _lessonsByWeeks.value.find {
                    if (it.second.isNotEmpty()) {
                        var firstLesson = it.second[0]
                        var lastLesson = it.second[it.second.size - 1]

                        firstLesson.date <= currentDate && currentDate <= lastLesson.date
                    }
                    else false
                }?.first

                if (currentWeekVal != null) {
                    _currentPage.intValue = currentWeekVal
                }
            }
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
                    teacher.value?.teacherId ?: ""
                )
            }

            setLessonsByWeeks()
        }

        getNowWatchLessons()
    }

    /**
     * Получить занятия.
     */
    fun getNowWatchLessons() {
        viewModelScope.launch(Dispatchers.IO) {
            _lessonsNotWatched.update {
                repository.getLessonsChanged(
                    teacher.value?.teacherId ?: ""
                )
            }
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
            val date = LocalDate.parse(it.second[0].date)

            if (date.dayOfWeek.value == 0) {
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
            val worker = DailyWorker(repository = repository)

            worker.doDailyWork(true)
        }
    }

    private var _watchingLessonId = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    var _watchingLesson = _watchingLessonId.flatMapLatest {
        repository.getLesson(_watchingLessonId.value)
    }

    val watchingLesson
        get() = _watchingLesson

    fun getLesson(lessonId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _watchingLessonId.update {
                lessonId ?: ""
            }
        }
    }

    fun addValueToCurrentPage(value: Int) {
        val newValue = _currentPage.intValue + value

        if (lessonsByWeeks.value.find {
                it.first == newValue
            } != null) {
            _currentPage.intValue += value
        }
    }

    /**
     * Загрузить расписание.
     */
    fun loadNewSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentDateTime = LocalDateTime.now()
            var data = repository.getWeek(LocalDate.now())

            if (data == null) {
                val nextDay = currentDateTime.plusDays(1)

                data = repository.getWeek(nextDay.toLocalDate())
            }

            if (data == null) {
                val dailyWorker = DailyWorker(repository)
                val lessons = dailyWorker.loadNewAndCheckWithOld()
                dailyWorker.saveLessons(lessons)
            }
        }
    }
}