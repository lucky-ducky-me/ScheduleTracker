package com.example.scheduletrackervyatsu.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.ui.components.Week
import com.example.scheduletrackervyatsu.ui.uiData.FiltersSectionData

@Composable
fun ScheduleSection(
    modifier: Modifier = Modifier,
    filtersSectionData: FiltersSectionData,
    onAcceptButtonClick: () -> Unit,
    lessonsByWeeks: List<Pair<Int, List<LessonEntity>>>,
    onTest: (() -> Unit)?,
) {
    var currentPage by rememberSaveable {
        mutableIntStateOf(0)
    }

    LazyColumn (
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
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

        val currentWeek = lessonsByWeeks.find {
            it.first == currentPage
        }

        if (onTest != null) {
            item {
                Button(
                    modifier = Modifier,

                    onClick = {
                        onTest()
                    }
                ) {
                    Text("Применить")
                }
            }
        }

        item {
            if (currentWeek != null) {
                Week(
                    lessons = currentWeek.second,
                    currentIndex = currentPage,
                    startIndex = lessonsByWeeks[0].first,
                    lastIndex = lessonsByWeeks[lessonsByWeeks.size - 1].first,
                    onBackArrowClick = {
                        currentPage--
                    },
                    onForwardArrowClick = {
                        currentPage++
                    }
                )
            }
        }
    }
}