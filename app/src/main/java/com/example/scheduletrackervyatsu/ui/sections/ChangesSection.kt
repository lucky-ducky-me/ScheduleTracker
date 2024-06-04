package com.example.scheduletrackervyatsu.ui.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.ui.uiData.FiltersSectionData

@Composable
fun ChangesSection(
    modifier: Modifier = Modifier,
    filtersSectionData: FiltersSectionData,
    onAcceptButtonClick: () -> Unit,
    onWatchChangeClick: (String) -> Unit,
    lessonsNotWatched: List<LessonEntity>,
    onWatchAllChangesButtonClick: (String) -> Unit,
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

        item {
            Button(
                modifier = Modifier.width(200.dp),
                onClick = {
                    onWatchAllChangesButtonClick(
                        filtersSectionData.teacher?.teacherId ?: "")
                },
                colors = ButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)
            ) {
                Text(
                    text = "Просмотреть все изменения",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (lessonsNotWatched.isEmpty()) {
            item {
                Text(
                    text = "Изменения не найдены.",
                    style = MaterialTheme.typography.titleLarge)
            }
        }

        items(lessonsNotWatched) {
            SingleLessonSection(lessonEntity = it, onWatchChangeClick = onWatchChangeClick)
        }
    }
}
