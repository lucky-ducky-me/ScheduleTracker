package com.example.scheduletrackervyatsu.ui.components.sections

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.domain.SettingsViewModel
import com.example.scheduletrackervyatsu.ui.components.AddingDepartmentDialog
import com.example.scheduletrackervyatsu.ui.components.AddingTeacherDialog
import com.example.scheduletrackervyatsu.ui.components.TeacherSettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val settings = settingsViewModel.settings.collectAsState(initial = emptyList()).value
    val departments = settingsViewModel.departments.collectAsState(initial = emptyList()).value
    val teachers = settingsViewModel.teachers.collectAsState(initial = emptyList()).value
    var openAddingDialogState = settingsViewModel.openAddingDialogState.collectAsState().value

    var openAddingTeacherDialog by remember { mutableStateOf(false) }

    if (openAddingTeacherDialog) {
        AddingTeacherDialog(
            onDismissRequest = { openAddingTeacherDialog = false },
            onConfirmation = {
                teacherId: String, departmentId: String ->
                openAddingTeacherDialog = false
                settingsViewModel.addDepartmentForTeacher(teacherId, departmentId)
            },
            dialogTitle = "Добавить преподавателя:",
            teachers = teachers,
            departments = departments
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
                    IconButton(onClick = { openAddingTeacherDialog = !openAddingTeacherDialog }) {
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

                TeacherSettingItem(
                    setting = setting,
                    onDeleteTeacherButtonClick = {
                        teacherId: String ->
                            settingsViewModel.deleteTeacher(teacherId)
                    },
                    onAddDepartmentButtonClick = {
                        teacherId: String ->
                            settingsViewModel.changeAddingDialogState(
                                isOpen = true,
                                teacherId= teacherId)
                    },
                    onDeleteDepartmentButtonClick = {
                        teacherId: String, departmentId: String ->
                        settingsViewModel.deleteDepartmentForTeacher(teacherId, departmentId)
                    }
                )
            }
        }
    }
}