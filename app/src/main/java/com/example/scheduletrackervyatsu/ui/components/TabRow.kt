package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    onTabClick: (TabRowDirection) -> Unit,
    tabRowDirection: TabRowDirection,

    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary).padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Расписание",
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onTabClick(TabRowDirection.Schedule)
                },
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            style = if (tabRowDirection == TabRowDirection.Schedule)
                    MaterialTheme.typography.titleLarge
                else
                    MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Изменения",
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onTabClick(TabRowDirection.Changes)
                },
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            style =  if (tabRowDirection == TabRowDirection.Changes)
                MaterialTheme.typography.titleLarge
            else
                MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Настройки",
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onTabClick(TabRowDirection.Settings)
                },
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            style =  if (tabRowDirection == TabRowDirection.Settings)
                MaterialTheme.typography.titleLarge
            else
                MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = {
            onTabClick(TabRowDirection.Info)
        }) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

enum class TabRowDirection {
    Schedule,
    Changes,
    Settings,
    Info
}

