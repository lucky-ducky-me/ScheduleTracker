package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity

@Composable
fun Day(
    modifier: Modifier = Modifier,
    lessons: List<LessonEntity>,
    onWatchChangeClick: (String) -> Unit) {

    val header = lessons[0].dayOfWeek + " " + lessons[0].date

    Column(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(5))
            .clip(shape = RoundedCornerShape(5))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            text = header,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        lessons.forEachIndexed {index, lesson ->
            var rowModifier = Modifier.fillMaxWidth()

            if (index != lessons.size - 1) {
               // rowModifier = rowModifier.border(BorderStroke(1.dp, Color.Black))
            }

            if (index % 2 == 0) {
                rowModifier = rowModifier.background(MaterialTheme.colorScheme.tertiaryContainer)
            }

            if (!lesson.isStatusWatched) {
                if (lesson.lessonStatusId == 4) {
                    rowModifier = rowModifier.background(Color.Red)
                }
                if (lesson.lessonStatusId == 2) {
                    rowModifier = rowModifier.background(Color.Green)
                }
            }

            Row(
                modifier = rowModifier
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = lesson.time,
                        modifier = modifier
                            .fillMaxWidth(0.2f)
                            .fillMaxHeight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (lesson.office != null) {
                        Text(
                            text = lesson.office,
                            modifier = Modifier.fillMaxWidth(),
                            color = if (lesson.lessonStatusId != 5
                                && lesson.lessonStatusId != 6
                                || lesson.isStatusWatched)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = lesson.data,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = if (lesson.lessonStatusId != 3
                            && lesson.lessonStatusId != 6
                            || lesson.isStatusWatched)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else Color.LightGray,
                        textAlign = TextAlign.Center)
                }
            }
        }
    }
}