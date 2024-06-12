package com.example.scheduletrackervyatsu.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.ui.uiData.FiltersSectionData

/**
 * Секция с фильтром.
 * @param filtersSectionData данные для секции фильтров.
 * @param onAcceptButtonClick обработчик кнопки принять.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSection(
    modifier: Modifier = Modifier,
    filtersSectionData: FiltersSectionData,
    onAcceptButtonClick: () -> Unit
) {
    var expandedTeacher by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp),

        ) {

        // Выпадающий список с преподавателями
        ExposedDropdownMenuBox(
            expanded = expandedTeacher,
            onExpandedChange = { expandedTeacher = !expandedTeacher },
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextField(
                value = filtersSectionData.teacher?.fio ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTeacher)
                },
                label = { Text(text = "Выберите преподавателя: ") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge,
            )

            ExposedDropdownMenu(
                expanded = expandedTeacher,
                onDismissRequest = {
                    expandedTeacher = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                filtersSectionData.trackingTeachers.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Text(
                                text = item.fio,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                        onClick = {
                            expandedTeacher = false
                            filtersSectionData.onSelectedTeacherChange(item.teacherId)
                        }
                    )
                }
            }
        }
    }
}