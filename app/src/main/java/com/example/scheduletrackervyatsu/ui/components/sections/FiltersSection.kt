package com.example.scheduletrackervyatsu.ui.components.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.ui.uiData.FiltersSectionData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSection(
    modifier: Modifier = Modifier,
    filtersSectionData: FiltersSectionData,
    onAcceptButtonClick: () -> Unit
) {
    val context = LocalContext.current

    var expandedTeacher by remember { mutableStateOf(false) }
    var expandedDepartment by remember { mutableStateOf(false) }

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
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedTeacher,
                onDismissRequest = {
                    expandedTeacher = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                filtersSectionData.teachers.forEachIndexed { index, item ->
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
                            Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                            filtersSectionData.onSelectedTeacherChange(item.teacherId)
                        }
                    )
                }
            }
        }

        // Выпадающий список с кафедрами преподавателя
        ExposedDropdownMenuBox(
            expanded = expandedDepartment,
            onExpandedChange = { expandedDepartment = !expandedDepartment },
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextField(
                value = filtersSectionData.department?.name ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDepartment) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedDepartment,
                onDismissRequest = {
                    expandedDepartment = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                filtersSectionData.departments.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Text(
                                text = item.name,
                            )
                        },
                        onClick = {
                            expandedDepartment = false
                            Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                            filtersSectionData.onSelectedDepartmentChange(item.departmentId)
                        }
                    )
                }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            colors = ButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            onClick = {
                onAcceptButtonClick()
            }
        ) {
            Text("Применить фильтр")
        }
    }
}