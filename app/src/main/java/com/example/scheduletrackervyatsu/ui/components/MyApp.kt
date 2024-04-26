package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.scheduletrackervyatsu.ui.theme.ScheduleTrackerVyatsuTheme

@Composable
fun MyApp(
    modifier: Modifier = Modifier,
) {
    Section(title = "Расписание")
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun MyAppPreview() {
    ScheduleTrackerVyatsuTheme {
        MyApp(modifier = Modifier.fillMaxSize())
    }
}