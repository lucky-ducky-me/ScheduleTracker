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

/**
 * Диалоговое окно с информаций об использовании приложения.
 * @param onConfirmation обработчик нажатия кнопки Подтвердить.
 * @param dialogTitle заголовок окна.
 */
@Composable
fun InfoDialog(
    modifier: Modifier = Modifier,
    onConfirmation: () -> Unit,
    dialogTitle: String,
) {
    val messages = listOf<String>(
        "В разделе \"Расписание\" можно просматривать расписание выбранных преподавателей.\n",
        "В разделе \"Изменения\"  можно просматривать изменения в расписании выбранных преподавателей.\n",
        "В разделе \"Настройки\" осуществляется выбор преподавателей, у которых будет отображаться" +
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
                            textAlign = TextAlign.Left
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