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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity

@Composable
fun Day(
    modifier: Modifier = Modifier,
    name:String,
    lessonsList: List<LessonEntity>) {

    val date = lessonsList[0].dateTime.split("T")[0]

    Column(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(5))
            .clip(shape = RoundedCornerShape(5))
            .background(MaterialTheme.colorScheme.secondaryContainer)

    ) {
        Text(
            text = date,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        lessonsList.forEach { lesson ->

            Row(modifier = modifier.fillMaxWidth()
//                .border(
//                    width = 16.dp,
//                    color = Color.Black,
//                    shape = object: Shape {
//                        override fun createOutline(
//                            size: Size,
//                            layoutDirection: LayoutDirection,
//                            density: Density
//                        ): Outline {
//                            return Outline.Generic(
//                                // Just left border
//                                Path().apply {
//                                    moveTo(0f, 0f)
//                                    lineTo(size.width, 0f)
//                                    lineTo(size.width, 1f)
//                                    lineTo(0f, 1f)
//                                    close()
//                                }
//                            )
//                        }
//                    }
//                )
            ) {
                Text(
                    text = lesson.dateTime.split("T")[1],
                    modifier = modifier.fillMaxWidth(0.2f).padding(8.dp).fillMaxHeight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Text(
                    text = lesson.name,
                    modifier = modifier.fillMaxWidth().padding(8.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center)
            }
        }
    }
}