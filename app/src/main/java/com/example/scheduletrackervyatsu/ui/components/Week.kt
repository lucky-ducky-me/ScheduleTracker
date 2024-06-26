package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import java.text.DateFormatSymbols
import java.time.LocalDate

/**
 * Неделя.
 * @param lessons список занятий.
 * @param startIndex индекс первой недели.
 * @param currentIndex индекс выбранной недели.
 * @param lastIndex индекс последней недели.
 * @param onBackArrowClick обработчик нажатия перехода на предыдущую неделю.
 * @param onForwardArrowClick обработчик нажатия перехода на следующую неделю.
 * @param onWatchChangeClick обработчик иконки просмотра изменения.
 * @param onChangeLessonClick обработчик нажатия на изменённое занятие.
 * @param currentWeek текущая учебная (1-я или 2-я) неделя.
 */
@Composable
fun Week(
    modifier: Modifier = Modifier,
    lessons: List<LessonEntity>,
    startIndex: Int,
    currentIndex: Int,
    lastIndex: Int,
    onBackArrowClick: () -> Unit,
    onForwardArrowClick: () -> Unit,
    onWatchChangeClick: (String) -> Unit,
    onChangeLessonClick: (String) -> Unit,
    currentWeek: Int
) {
    val map = mutableMapOf<String, List<LessonEntity>>()

    lessons.forEach {
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

    val firstDate = LocalDate.parse(lessonsByDays[0].first)
    val lastDate = LocalDate.parse(lessonsByDays[lessonsByDays.size - 1].first)

    val header = firstDate.dayOfMonth.toString() + " " +
            DateFormatSymbols().months[firstDate.month.value - 1] + " - "  +
            lastDate.dayOfMonth.toString() + " " +
            DateFormatSymbols().months[lastDate.month.value - 1] + "\n" +
            ((currentWeek) % 2 + 1).toString() + " неделя"


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { onBackArrowClick() },
                enabled = currentIndex != startIndex
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = header,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = { onForwardArrowClick() },
                enabled = currentIndex != lastIndex
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowRight,
                    contentDescription = null
                )
            }
        }

        lessonsByDays.forEach {
            Day(lessons = it.second,
                onWatchChangeClick = onWatchChangeClick,
                onChangeLessonClick = onChangeLessonClick)
        }
    }
}