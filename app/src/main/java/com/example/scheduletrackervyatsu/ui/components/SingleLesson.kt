package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.compose.changeAddedColor
import com.example.compose.changeDeletedColor
import com.example.scheduletrackervyatsu.data.entities.LessonEntity

@Composable
fun SingleLesson(
    modifier: Modifier = Modifier,
    lessonEntity: LessonEntity,
    onWatchChangeClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(5))
            .clip(shape = RoundedCornerShape(5))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Дата: " + lessonEntity.date,
            textAlign = TextAlign.Center
        )

        var beforeColumnModifier = Modifier.fillMaxWidth()

        if (!lessonEntity.isStatusWatched) {
            if (lessonEntity.lessonStatusId == 4) {
                beforeColumnModifier = beforeColumnModifier.background(changeDeletedColor)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = beforeColumnModifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "До: ",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = lessonEntity.time,
                        modifier = modifier
                            .fillMaxWidth(0.2f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = lessonEntity.oldOffice ?: "",
                        modifier = Modifier.fillMaxWidth(),
                        color = if (lessonEntity.lessonStatusId != 5
                            && lessonEntity.lessonStatusId != 6
                            || lessonEntity.isStatusWatched
                        )
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = lessonEntity.oldData ?: "",
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = if (lessonEntity.lessonStatusId != 3
                            && lessonEntity.lessonStatusId != 6
                            || lessonEntity.isStatusWatched
                        )
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        var afterColumnModifier = Modifier.fillMaxWidth()

        if (!lessonEntity.isStatusWatched) {
            if (lessonEntity.lessonStatusId == 2) {
                afterColumnModifier = afterColumnModifier.background(changeAddedColor)
            }
        }

        Column(
            modifier = afterColumnModifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Сейчас: ",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = lessonEntity.time,
                        modifier = modifier
                            .fillMaxWidth(0.2f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (lessonEntity.office != null) {
                        Text(
                            text = lessonEntity.office,
                            modifier = Modifier.fillMaxWidth(),
                            color = if (lessonEntity.lessonStatusId != 5
                                && lessonEntity.lessonStatusId != 6
                                || lessonEntity.isStatusWatched
                            )
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = lessonEntity.data,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = if (lessonEntity.lessonStatusId != 3
                            && lessonEntity.lessonStatusId != 6
                            || lessonEntity.isStatusWatched
                        )
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        IconButton(
            onClick = {
                onWatchChangeClick(lessonEntity.lessonId)
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