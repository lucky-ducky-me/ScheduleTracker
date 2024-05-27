package com.example.scheduletrackervyatsu.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingTeacherDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit,
    dialogTitle: String,
    teachers: List<TeacherEntity>,
    departments: List<DepartmentEntity>,
) {
    val context = LocalContext.current

    var teacherFIO by remember {
        mutableStateOf("")
    }

    val teachersFIOs = teachers.map { it.fio  }

    var expandedTeacher by remember { mutableStateOf(false) }

    var expandedDepartment by remember { mutableStateOf(false) }

    val departmentNames = departments.map { it.name }

    var departmentName by remember {
        mutableStateOf( "")
    }

    var isTeacherInputIncorrect by remember { mutableStateOf(false) }
    var isDepartmentInputIncorrect by remember { mutableStateOf(false) }

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
                    if (isTeacherInputIncorrect) {
                        Text(
                            text = "Преподаватель введён некорректно:",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedTeacher,
                        onExpandedChange = {
                            expandedTeacher = !expandedTeacher
                        },
                        ) {
                        TextField(
                            value = teacherFIO,
                            onValueChange = {
                                teacherFIO = it
                                isTeacherInputIncorrect = false
                            },
                            label = { Text(text = "Выберите преподавателя: ") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTeacher)
                            },
                            modifier = Modifier.menuAnchor(),
                        )

                        val filteredOptions = teachersFIOs.filter {
                            it.contains(teacherFIO, ignoreCase = true) }

                        if (filteredOptions.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expandedTeacher,
                                onDismissRequest = {},
                                modifier = Modifier.height(200.dp)
                            ) {
                                filteredOptions.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item) },
                                        onClick = {
                                            teacherFIO = item
                                            val teacher = teachers.find { it.fio == teacherFIO }

                                            if (teacher != null) {
                                                val department = departments.find {
                                                    it.departmentId == teacher.defaultDepartment }?.name

                                                if (department != null) {
                                                    departmentName = department
                                                }
                                            }

                                            expandedTeacher = false
                                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    }


                    if (isDepartmentInputIncorrect) {
                        Text(
                            text = "Кафедра введена некорректно:",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error,
                        )
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
                                isDepartmentInputIncorrect = false
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
                        val teacherId = teachers.find { it.fio == teacherFIO }?.teacherId
                        val departmentId = departments.find { it.name == departmentName }?.departmentId

                        if (teacherId == null) {
                            isTeacherInputIncorrect = true
                        }
                        else if (departmentId == null) {
                            isDepartmentInputIncorrect = true
                        }
                        else {
                            onConfirmation(
                                teacherId,
                                departmentId)
                        }
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
}