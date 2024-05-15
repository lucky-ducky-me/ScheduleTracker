package com.example.scheduletrackervyatsu.ui.components

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingTeacherDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit,
    dialogTitle: String,
    teachers: List<TeacherEntity>,
    departments: List<DepartmentEntity>
) {
    val context = LocalContext.current

    var teacherFIO by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()

    val teachersFIOs = teachers.map { it.fio  }

    val myTooltipState = rememberTooltipState(isPersistent = true)

    var expanded by remember { mutableStateOf(false) }

    var expandedDepartment by remember { mutableStateOf(false) }

    var departmentNames = departments.map { it.name }

    var departmentName by remember {
        mutableStateOf("")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()

    ) {
        AlertDialog(
            icon = {
            },
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ){
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        },

                        ) {
                        TextField(
                            value = teacherFIO,
                            onValueChange = {
                                teacherFIO = it
                            },
                            label = { Text(text = "Выберите кафедру: ") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier.menuAnchor(),

                            )

                        val filteredOptions = teachersFIOs.filter {
                            it.contains(teacherFIO, ignoreCase = true) }

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
                                            teacherFIO = item
                                            expanded = false
                                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedDepartment,
                        onExpandedChange = {
                            expandedDepartment = !expandedDepartment
                        },

                        ) {
                        TextField(
                            value = departmentName,
                            onValueChange = {
                                departmentName = it
                            },
                            label = { Text(text = "Выберите кафедру: ") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDepartment)
                            },
                            modifier = Modifier.menuAnchor(),

                            )

                        val filteredOptions = departmentNames.filter {
                            it.contains(departmentName, ignoreCase = true) }

                        if (filteredOptions.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expandedDepartment,
                                onDismissRequest = {},
                                modifier = Modifier.height(200.dp)
                            ) {
                                filteredOptions.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item) },
                                        onClick = {
                                            departmentName = item
                                            expandedDepartment = false
                                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
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
                        onConfirmation(
                            teachers.find { it.fio == teacherFIO }?.teacherId ?: "",
                            departments.find { it.name == departmentName }?.departmentId ?: "")
                    }
                ) {
                    Text("Потвердить")
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
}