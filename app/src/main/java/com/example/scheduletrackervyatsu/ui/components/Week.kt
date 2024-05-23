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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity

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
    onChangeLessonClick: (String) -> Unit
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

    val header = lessonsByDays[0].first + "  -  " + lessonsByDays[lessonsByDays.size - 1].first

    Column(
        modifier = modifier,
            //.border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(5))
            //.clip(shape = RoundedCornerShape(5))
            //.background(MaterialTheme.colorScheme.secondaryContainer),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Row(
            horizontalArrangement = Arrangement.Center
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

            Text(text = header)

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