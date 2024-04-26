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
import com.example.scheduletrackervyatsu.ui.theme.ScheduleTrackerVyatsuTheme

@Composable
fun Day(
    modifier: Modifier = Modifier,
    name:String,
    lessonsList: List<String> = List(7) {"$it lesson"}) {
    val intervals: List<String> = List(7) {"$it"}

    Column(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(5))
    ) {
        Text(text = name, modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            , textAlign = TextAlign.Center)
        intervals.indices.forEach { index ->
            Row(modifier = modifier.padding(8.dp).fillMaxWidth()) {
                Text(text = intervals[index], modifier.fillMaxWidth(0.2f))
                Text(text = lessonsList[index], modifier.fillMaxWidth())
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DayPreview() {
    ScheduleTrackerVyatsuTheme {
        Day(Modifier, "Понедельник")
    }
}