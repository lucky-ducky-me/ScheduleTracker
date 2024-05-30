package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun InfoDialog(
    modifier: Modifier = Modifier,
    onConfirmation: () -> Unit,
    dialogTitle: String,
) {
    val messages = listOf<String>(
        "В разделе расписания можно просматривать расписание выбранных преподавателей.\n",
        "В разделе изменений можно просматривать изменения в расписании выбранных преподавателей.\n",
        "В разделе настроек осуществляется выбор преподавателей, у которых будет отображаться" +
                " расписание и будет вестись его отслеживание, и другие служебные функции.\n",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        AlertDialog(
            modifier = Modifier
                .fillMaxHeight(0.5f),
            icon = {
            },
            title = {
                Text(text = dialogTitle)
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        //.background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(10.dp)
                ) {
                    items(messages) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            },
            onDismissRequest = {
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmation
                ) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {

            }
        )
    }
}