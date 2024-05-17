package com.example.scheduletrackervyatsu.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.ui.components.SingleLesson


@Composable
fun SingleLessonSection(
    modifier: Modifier = Modifier,
    lessonEntity: LessonEntity?,
    onWatchChangeClick: (String) -> Unit
) {
    if (lessonEntity == null) {
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        SingleLesson(lessonEntity = lessonEntity, onWatchChangeClick = onWatchChangeClick)
    }
}