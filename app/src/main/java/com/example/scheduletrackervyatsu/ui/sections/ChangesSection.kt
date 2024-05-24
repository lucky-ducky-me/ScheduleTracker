package com.example.scheduletrackervyatsu.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.ui.uiData.FiltersSectionData

@Composable
fun ChangesSection(
    modifier: Modifier = Modifier,
    filtersSectionData: FiltersSectionData,
    onAcceptButtonClick: () -> Unit,
    onWatchChangeClick: (String) -> Unit,
    lessonsNotWatched: List<LessonEntity>
) {
    LazyColumn (
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = true
    ) {
        item {
            FiltersSection(
                modifier = Modifier,
                onAcceptButtonClick = {
                    onAcceptButtonClick()
                },
                filtersSectionData = filtersSectionData
            )
        }

        items(lessonsNotWatched) {
            SingleLessonSection(lessonEntity = it, onWatchChangeClick = onWatchChangeClick)
        }
    }
}
