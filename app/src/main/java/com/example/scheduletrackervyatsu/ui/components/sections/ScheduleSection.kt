package com.example.scheduletrackervyatsu.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.ui.components.Day
import com.example.scheduletrackervyatsu.ui.uiData.FiltersSectionData
import java.time.LocalDate

@Composable
fun ScheduleSection(
    modifier: Modifier = Modifier,
    filtersSectionData: FiltersSectionData,
    onAcceptButtonClick: () -> Unit,
    onTestButtonClick: () -> Unit,
    lessons: List<LessonEntity>
) {
    var splitList = lessons.map {it.dateTime.split("T")[0]}.toSet()

    val map = mutableMapOf<String, List<LessonEntity>>()

    lessons.forEach {
        lesson ->
        val date = lesson.dateTime.split("T")[0]

        if (!map.containsKey(date)) {
            map[date] = emptyList()
        }

        map[date] = map[date]!!.plus(listOf(lesson))
    }

    val temp = map.toList()


    LazyColumn (
        modifier = modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = true
    ) {
        item {
            FiltersSection(
                modifier = Modifier,
                selectedDepartment = filtersSectionData.department,
                selectedTeacher =  filtersSectionData.teacher,
                selectedDateTimeInterval = filtersSectionData.datetimeInterval,
                onSelectedDepartmentChange = filtersSectionData.onSelectedDepartmentChange,
                onSelectedTeacherChange = filtersSectionData.onSelectedTeacherChange,
                onSelectedDateIntervalChange = filtersSectionData.onSelectedDateTimeIntervalChange,
                departments = filtersSectionData.departments,
                teachers  = filtersSectionData.trackingTeachers,
                datesIntervals = filtersSectionData.datetimeIntervals,
                onAcceptButtonClick = {
                    onAcceptButtonClick()
                }
            )
        }
        item {
            Button(onClick = {
                onTestButtonClick()
            }) {
                Text("Выполнить парсинг")
            }
        }

        items(temp) {
            tem -> Day(name = "Пока не доне", lessonsList = tem.second)
        }
    }
}