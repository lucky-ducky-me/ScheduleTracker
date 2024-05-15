package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import java.time.LocalDate
import com.example.scheduletrackervyatsu.ui.theme.ScheduleTrackerVyatsuTheme
import java.time.DayOfWeek

@Composable
fun Day(
    modifier: Modifier = Modifier,
    name:String,
    lessonsList: List<LessonEntity>) {

    var date = lessonsList[0].dateTime.split("T")[0]

    Column(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(5))
    ) {
        Text(
            text = date,
            modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            , textAlign = TextAlign.Center)

        lessonsList.forEach { lesson ->
            Row(modifier = modifier.padding(8.dp).fillMaxWidth()) {
                Text(text = lesson.dateTime.split("T")[1]
                    , modifier.fillMaxWidth(0.2f))
                Text(text = lesson.name, modifier.fillMaxWidth())
            }
        }
    }
}