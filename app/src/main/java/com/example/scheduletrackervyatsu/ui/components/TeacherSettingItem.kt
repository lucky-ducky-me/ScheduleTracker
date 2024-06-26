package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity

/**
 * Элемент с преподавателями в настройках.
 * @param setting список отслеживаемых преподавателей с кафедрами.
 * @param onAddDepartmentButtonClick обработчик нажатия кнопки Добавить кафедру.
 * @param onDeleteDepartmentButtonClick обработчик нажатия кнопки Удалить кафедру.
 * @param onDeleteTeacherButtonClick обработчик нажатия кнопки Удалить преподавателя.
 */
@Composable
fun TeacherSettingItem(
    modifier: Modifier = Modifier,
    setting: Pair<TeacherEntity, List<DepartmentEntity>>,
    onDeleteTeacherButtonClick: (String) -> Unit,
    onAddDepartmentButtonClick: (String) -> Unit,
    onDeleteDepartmentButtonClick: (String, String) -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)

    ) {

        Row(modifier = modifier.fillMaxWidth()) {
            TextField(
                modifier = modifier
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    .fillMaxWidth(),
                onValueChange = {},
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                value = setting.first.surname + " " +
                        setting.first.name + " " +
                        (setting.first.patronymic ?: ""),
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
                        IconButton(onClick = {
                            onDeleteTeacherButtonClick(setting.first.teacherId)
                        }) {
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
            DepartmentSettingItem(
                setting = setting,
                onAddButtonClick = onAddDepartmentButtonClick,
                onDeleteButtonClick = {
                    departmentId: String ->
                    onDeleteDepartmentButtonClick(setting.first.teacherId, departmentId)
                }
            )
        }
    }
}