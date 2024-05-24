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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.domain.AddingTeacherDialogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingTeacherDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit,
    dialogTitle: String,
    teachers: List<TeacherEntity>,
    departments: List<DepartmentEntity>,
    addingTeacherDialogViewModel: AddingTeacherDialogViewModel = viewModel()
) {
    val selectedDepartment = addingTeacherDialogViewModel.department.collectAsState(initial = null).value

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
        mutableStateOf(selectedDepartment?.name ?: "")
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
                            label = { Text(text = "Выберите преподавателя: ") },
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
                                            val teacher =  teachers.find { it.fio == teacherFIO }
                                            val department = departments.find {it.departmentId == teacher?.defaultDepartment}

                                            if (department != null) {
                                                addingTeacherDialogViewModel.onSelectTeacherClick(department)
                                            }

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
                            value = selectedDepartment?.name ?: "",
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
                                            val department = departments.find {it.name == departmentName}

                                            if (department != null) {
                                                addingTeacherDialogViewModel.onSelectTeacherClick(department)
                                            }

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
                            selectedDepartment?.departmentId ?: "")
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