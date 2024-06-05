package com.example.scheduletrackervyatsu.ui.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.ui.components.Week
import com.example.scheduletrackervyatsu.ui.uiData.FiltersSectionData

/**
 * Раздел с расписанием.
 * @param filtersSectionData данные для секции фильтров.
 * @param onAcceptButtonClick обработчик кнопки принять.
 * @param lessonsByWeeks занятия поделённые по неделям.
 * @param onWatchChangeClick обработчик иконки просмотра изменения.
 * @param onChangeLessonClick обработчик нажатия на изменённое занятие.
 * @param onCurrentPagePrevious обработчик нажатия перехода на предыдущую неделю.
 * @param onCurrentPageNext обработчик нажатия перехода на следующую неделю.
 * @param onCheckScheduleOnChanges обработчик нажатия кнопки проверки изменений.
 */
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
    onCheckScheduleOnChanges: () -> Unit,
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

        item {
            Button(
                modifier = Modifier.width(200.dp),
                onClick = {
                    onCheckScheduleOnChanges()
                },
                colors = ButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
            ) {
                Text(
                    text = "Проверить изменения",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
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
                    onChangeLessonClick = onChangeLessonClick,
                    currentWeek = currentWeek.first
                )
            }
            else {
                Text(
                    text = "Расписание не найдено.",
                    style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}