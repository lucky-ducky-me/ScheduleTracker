package com.example.scheduletrackervyatsu.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onWatchChangeClick: (String) -> Unit,
    onChangeLessonClick: (String) -> Unit,
    onTest: (() -> Unit)?,
    currentPage: Int = 0,
    onCurrentPagePrevious: () -> Unit,
    onCurrentPageNext: () -> Unit,
) {
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
                onAcceptButtonClick = {
                    onAcceptButtonClick()
                },
                filtersSectionData = filtersSectionData
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
                    Text("ТЕСТОВАЯ КНОПКА")
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
                        onCurrentPagePrevious()
                    },
                    onForwardArrowClick = {
                        onCurrentPageNext()
                    },
                    onWatchChangeClick = onWatchChangeClick,
                    onChangeLessonClick = onChangeLessonClick
                )
            }
        }
    }
}