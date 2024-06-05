package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.changeAddedColor
import com.example.compose.changeDeletedColor
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * День в расписании.
 * @param lessons список занятий.
 * @param onWatchChangeClick обработчик иконки просмотра изменения.
 * @param onChangeLessonClick обработчик нажатия на изменённое занятие.
 */
@Composable
fun Day(
    modifier: Modifier = Modifier,
    lessons: List<LessonEntity>,
    onWatchChangeClick: (String) -> Unit,
    onChangeLessonClick: (String) -> Unit) {

    val date = LocalDate.parse(lessons[0].date)

    val header = DateFormatSymbols().weekdays[date.dayOfWeek.value + 1] + " " +
            date.dayOfMonth.toString() + " " +
    DateFormatSymbols().months[date.month.value - 1] + " "

    Column(
        modifier = modifier
            .border(
                border =
                if (date == LocalDate.now())
                    BorderStroke(5.dp, MaterialTheme.colorScheme.tertiary)
                else
                    BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer),
                shape = RoundedCornerShape(5))
            .clip(shape = RoundedCornerShape(5))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            text = header,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.titleLarge
        )

        var curIndex = 0

        lessons.forEachIndexed { index, lesson ->
            var rowModifier = Modifier.fillMaxWidth()

            val lessonDateTime =
                LocalDateTime.parse(lesson.date + "T" + lesson.time)
            val currentDateTime = LocalDateTime.now()
            val lessonDuration = 90
            val lessonEndDateTime = lessonDateTime.plusMinutes(lessonDuration.toLong())

            if (currentDateTime in lessonDateTime..lessonEndDateTime) {
                rowModifier = rowModifier.border(
                    border = BorderStroke(5.dp, MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(5))
                    .clip(shape = RoundedCornerShape(5))
            }

            if (index > 0 && lessons[index - 1].time == lessons[index].time) {
                curIndex--
            }

            if (curIndex % 2 == 0) {
                rowModifier = rowModifier.background(MaterialTheme.colorScheme.surface)
            }

            if (!lesson.isStatusWatched) {
                if (lesson.lessonStatusId == 4) {
                    rowModifier = rowModifier.background(changeDeletedColor)
                }

                if (lesson.lessonStatusId == 2) {
                    rowModifier = rowModifier.background(changeAddedColor)
                }

                rowModifier = rowModifier.clickable { onChangeLessonClick(lesson.lessonId) }
            }

            Row(
                modifier = rowModifier
            ) {
                Column(
                    modifier = Modifier.padding(5.dp).fillMaxWidth(0.2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (index == 0 || lessons[index - 1].time != lessons[index].time) {
                        Text(
                            text = lesson.time,
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    if (!lesson.isStatusWatched) {
                        IconButton(
                            onClick = {
                                onWatchChangeClick(lesson.lessonId)
                            },
                            enabled = true
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    }
                }


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(5.dp)
                ) {
                    if (lesson.office != null) {
                        Text(
                            text = lesson.office,
                            modifier = Modifier.fillMaxWidth(),
                            color = if (lesson.lessonStatusId != 5
                                && lesson.lessonStatusId != 6
                                || lesson.isStatusWatched)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (lesson.data.isNotEmpty()) {
                        Text(
                            text = lesson.data,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (lesson.lessonStatusId != 3
                                && lesson.lessonStatusId != 6
                                || lesson.isStatusWatched
                            )
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Left
                        )
                    }
                }
            }

            curIndex++
        }
    }
}