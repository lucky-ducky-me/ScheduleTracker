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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.changeAddedColor
import com.example.compose.changeDeletedColor
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import java.text.DateFormatSymbols
import java.time.LocalDate

/**
 * Элемент занятия.
 * @param lessonEntity сущность занятия.
 * @param onWatchChangeClick обработчик нажатия на изменённое занятие.
 */
@Composable
fun SingleLesson(
    modifier: Modifier = Modifier,
    lessonEntity: LessonEntity,
    onWatchChangeClick: (String) -> Unit
) {
    val date = LocalDate.parse(lessonEntity.date)

    val header = DateFormatSymbols().weekdays[date.dayOfWeek.value + 1] + " " +
            date.dayOfMonth.toString()  + " " +
            DateFormatSymbols().months[date.month.value - 1] + " "

    Column(
        modifier = Modifier.fillMaxWidth()
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer),
                shape = RoundedCornerShape(5))
            .clip(shape = RoundedCornerShape(5))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = header,
            textAlign = TextAlign.Center,
            modifier =Modifier.padding(5.dp),
            style = MaterialTheme.typography.titleLarge
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
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Text(
                text = "До: ",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
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
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyLarge
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
                        else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
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
                        else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Left,
                        style = MaterialTheme.typography.bodyLarge
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
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
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
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyLarge
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
                            else MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
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
                        else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Left,
                        style = MaterialTheme.typography.bodyLarge
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