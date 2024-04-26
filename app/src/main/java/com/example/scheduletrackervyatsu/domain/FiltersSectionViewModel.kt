package com.example.scheduletrackervyatsu.domain

import android.icu.util.DateInterval
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class FiltersSectionViewModel(): ViewModel() {

    var departments = mutableListOf<String>(
        "Dep1",
        "Dep2",
        "Dep3",
        "Dep4",
        "Dep5",
        "Dep6",
        "Dep7",
        "Dep8",)
        .toMutableStateList()

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

    private var _department = mutableStateOf(departments.get(0))

    private var _teacher = mutableStateOf(teachers.get(0))

    private var _dateIntervals = mutableStateOf(dateIntervals.get(0))

    val department
        get() = _department

    val teacher
        get() = _teacher

    fun changeCurrentDepartment(department: String) {
        _department.value = department
        Log.d("value", department)
    }

    fun changeCurrentTeacher(teacher: String) {
        _teacher.value = teacher
    }

    fun changeDateInterval(dateInterval: String) {
        _dateIntervals.value = dateInterval
    }
}