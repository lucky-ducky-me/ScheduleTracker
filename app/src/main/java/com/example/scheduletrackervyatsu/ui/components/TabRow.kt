package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Навигационная панель.
 * @param onTabClick обработчик нажатия на элемент в навигационной панели
 * @param tabRowDirection текущее положение.
 */
@Composable
fun TabRow(
    modifier: Modifier = Modifier,
    onTabClick: (TabRowDirection) -> Unit,
    tabRowDirection: TabRowDirection,
    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var scheduleColumnModifier = Modifier.clickable { onTabClick(TabRowDirection.Schedule) }
            .weight(1f)

        if (tabRowDirection == TabRowDirection.Schedule) {
            scheduleColumnModifier = scheduleColumnModifier.background(MaterialTheme.colorScheme.inversePrimary)
        }

        Column(
            modifier = scheduleColumnModifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = {
                onTabClick(TabRowDirection.Schedule)
            }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        var changesColumnModifier = Modifier.clickable { onTabClick(TabRowDirection.Changes) }
            .weight(1f)

        if (tabRowDirection == TabRowDirection.Changes) {
            changesColumnModifier = changesColumnModifier.background(MaterialTheme.colorScheme.inversePrimary)
        }

        Column(
            modifier = changesColumnModifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = {
                onTabClick(TabRowDirection.Changes)
            }) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        var settingsColumnModifier = Modifier.clickable { onTabClick(TabRowDirection.Settings) }
            .weight(1f)

        if (tabRowDirection == TabRowDirection.Settings) {
            settingsColumnModifier = settingsColumnModifier.background(MaterialTheme.colorScheme.inversePrimary)
        }

        Column(
            modifier = settingsColumnModifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = {
                onTabClick(TabRowDirection.Settings)
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        IconButton(
            onClick = {
                onTabClick(TabRowDirection.Info)
            },
            modifier = Modifier.weight(0.5f)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Информация",
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

