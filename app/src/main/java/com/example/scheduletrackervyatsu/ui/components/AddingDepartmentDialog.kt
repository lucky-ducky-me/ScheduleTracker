package com.example.scheduletrackervyatsu.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity

/**
 * Диалоговое окно добавление кафедры.
 * @param onDismissRequest обработчик нажатия кнопки Отклонить.
 * @param onConfirmation обработчик нажатия кнопки Принять.
 * @param dialogTitle заголовок окна.
 * @param departments список кафедр.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingDepartmentDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmation: (String?) -> Unit,
    dialogTitle: String,
    departments: List<DepartmentEntity>
) {
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }

    val departmentNames = departments.map { it.name }

    var departmentName by remember {
        mutableStateOf("")
    }

    AlertDialog(
        icon = {},
        title = {
            Text(text = dialogTitle)
        },
        text = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },

            ) {
                TextField(
                    value = departmentName,
                    onValueChange = {
                        departmentName = it
                    },
                    label = { Text(text = "Выберите кафедру: ") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor(),

                    )

                val filteredOptions = departmentNames.filter { it.contains(departmentName, ignoreCase = true) }

                if (filteredOptions.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {},
                        modifier = Modifier.height(200.dp)
                    ) {
                        filteredOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    departmentName = item
                                    expanded = false
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(departments.find { it.name == departmentName }?.departmentId)
                }
            ) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Отменить")
            }
        }
    )
}