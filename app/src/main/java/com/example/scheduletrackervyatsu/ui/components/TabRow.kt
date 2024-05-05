package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TabRow(
    modifier: Modifier = Modifier,
    onTabClick: (TabRowDirection) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().height(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Расписание",
            modifier = Modifier.weight(1f).clickable {
                onTabClick(TabRowDirection.Schedule)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Text(
            "Изменения",
            modifier = Modifier.weight(1f).clickable {
                onTabClick(TabRowDirection.Changes)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            "Настройки",
            maxLines = 1,
            modifier = Modifier.weight(1f).clickable {
                onTabClick(TabRowDirection.Settings)
            },
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

enum class TabRowDirection {
    Schedule,
    Changes,
    Settings
}

