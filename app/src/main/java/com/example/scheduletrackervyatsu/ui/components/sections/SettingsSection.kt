package com.example.scheduletrackervyatsu.ui.components.sections

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.domain.AddingDepartmentDialogState
import com.example.scheduletrackervyatsu.domain.SettingsViewModel
import com.example.scheduletrackervyatsu.ui.components.AddingDepartmentDialog
import com.example.scheduletrackervyatsu.ui.components.Department

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    var settings = settingsViewModel.settings.collectAsState(initial = emptyList()).value
    var departments = settingsViewModel.departments.collectAsState(initial = emptyList()).value

    var mock = mapOf<TeacherEntity, DepartmentEntity>(
        TeacherEntity(teacherId = "c853b5eb-1377-4c9e-9fdc-27dc6c68d6a7",
            name = "test",
            surname = "Тестович",
            patronymic = null)
        to DepartmentEntity(departmentId = "5cd6cfcc-0a5b-415a-9047-1acb068327d1",
            "Кафедра биологии и методики обучения биологии (ОРУ)")
    )
//
//    val tableData = remember {
//        mutableStateListOf("Преподаватель 1", "Преподаватель 2")
//    }

    var openAddingDialogState = settingsViewModel.openAddingDialogState.collectAsState().value

    var openAlertDialog2 by remember { mutableStateOf(false) }

    if (openAlertDialog2) {
        AlertDialogExample(
            onDismissRequest = { openAlertDialog2 = false },
            onConfirmation = {
                openAlertDialog2 = false
                settingsViewModel.addNewTeacher("Test", "Тестович")
            },
            dialogTitle = "Добавить преподавателя:",
            dialogText = "",
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            item {
                Row(
                    modifier = modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = modifier.weight(0.9f),
                        text = "Список преподавателей",
                        textAlign = TextAlign.Center)
                    IconButton(onClick = { openAlertDialog2 = !openAlertDialog2 }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }


            }

            items(settings) {
                setting ->

                if (openAddingDialogState.isOpen) {
                    AddingDepartmentDialog(
                        onDismissRequest = { settingsViewModel.changeAddingDialogState(
                            isOpen = false,
                            teacherId = null,
                            departmentId = null) },
                        onConfirmation = {
                            if (openAddingDialogState.teacherId != null && it != null) {
                                settingsViewModel.addDepartmentForTeacher(
                                    openAddingDialogState.teacherId!!,
                                    it
                                )
                            }

                            settingsViewModel.changeAddingDialogState(
                                isOpen = false,
                                teacherId = null,
                                departmentId = null)
                        },
                        dialogTitle = "Укажите кафедру: ",
                        departments = departments
                    )
                }

                Column(
                    modifier = modifier.fillMaxWidth()
                ) {
                    var expanded by remember {
                        mutableStateOf(false)
                    }

                    Row(modifier = modifier.fillMaxWidth()) {
                        TextField(
                            modifier = modifier
                                .border(width = 1.dp, color = Color.Black)
                                .fillMaxWidth(),
                            onValueChange = {},
                            readOnly = true,
                            value = setting.first.name + setting.first.surname + " " + setting.first.patronymic,
                            trailingIcon = {
                                Row() {
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            imageVector =
                                            if (expanded)
                                                Icons.Default.ArrowDropUp
                                            else
                                                Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                        )
                    }

                    if (expanded) {
                        Department(
                            setting = setting,
                            onAddButtonClick = {
                                teacherId: String ->
                                settingsViewModel.changeAddingDialogState(
                                    isOpen = true,
                                    teacherId= teacherId)
                            },
                            onDeleteButtonClick = {

                            }
                        )
                    }
                }
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    var departmentName by remember {
        mutableStateOf("")
    }

    AlertDialog(
        icon = {
            //Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            TextField(
                modifier = Modifier
                    .border(width = 1.dp, color = Color.Black)
                    .fillMaxWidth(),
                onValueChange = {
                    departmentName = it
                },
                value = departmentName,
                trailingIcon = {})
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(departmentName)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}