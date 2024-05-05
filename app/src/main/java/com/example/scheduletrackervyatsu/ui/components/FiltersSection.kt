package com.example.scheduletrackervyatsu.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scheduletrackervyatsu.domain.SectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSection(
    modifier: Modifier = Modifier,
    selectedDepartment: String = "",
    selectedTeacher: String = "",
    onSelectedDepartmentChange: (String) -> Unit,
    onSelectedTeacherChange: (String) -> Unit,
    onSelectedDateIntervalChange: (String) -> Unit,
    departments: Array<String>,
    teachers: Array<String>,
    datesIntervals: Array<String>
) {
    val context = LocalContext.current

    var expandedDateInterval by remember { mutableStateOf(false) }
    var expandedTeacher by remember { mutableStateOf(false) }
    var expandedDepartment by remember { mutableStateOf(false) }

    var selectedTeacherState by rememberSaveable {
        mutableStateOf(selectedTeacher)
    }

    Column(
        modifier = Modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)

        ) {
        // Выпадающий список с преподавателями
        ExposedDropdownMenuBox(
            expanded = expandedTeacher,
            onExpandedChange = {
                expandedTeacher = !expandedTeacher
            }
        ) {
            TextField(
                value = selectedTeacherState,
                onValueChange = {
                    selectedTeacherState = it
                },
                label = { Text(text = "Выберите преподавателя") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTeacher)
               },
                modifier = Modifier.menuAnchor(),

            )

            val filteredOptions = teachers.filter { it.contains(selectedTeacherState, ignoreCase = true) }

            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expandedTeacher,
                    onDismissRequest = {}
                ) {
                    filteredOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedTeacherState = item
                                expandedTeacher = false
                                Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                onSelectedTeacherChange(selectedTeacherState)
                            }
                        )
                    }
                }
            }
        }

        // Выпадающий список с кафедрами преподавателя
        ExposedDropdownMenuBox(
            expanded = expandedDepartment,
            onExpandedChange = { expandedDepartment = !expandedDepartment },
            modifier = Modifier,
        ) {
            TextField(
                value = selectedDepartment,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDepartment) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedDepartment,
                onDismissRequest = {
                    expandedDepartment = false
                }
            ) {
                departments.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                            )
                        },
                        onClick = {
                            expandedDepartment = false
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            onSelectedDepartmentChange(item)
                        }
                    )
                }
            }
        }

        //  Выпадающий список с временным промежутком
        ExposedDropdownMenuBox(
            expanded = expandedDateInterval,
            onExpandedChange = { expandedDateInterval = !expandedDateInterval },
            modifier = Modifier,
        ) {
            TextField(
                value = datesIntervals[0],
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDateInterval)
                },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedDateInterval,
                onDismissRequest = { expandedDateInterval = false }
            ) {
                datesIntervals.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                            )
                        },
                        onClick = {
                            expandedDateInterval = false
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            onSelectedDateIntervalChange(item)
                        }
                    )
                }
            }
        }


        Button(onClick = {
            //TODO
        }) {
            Text("Применить")
        }
    }
}

@Preview
@Composable
fun FiltersSectionPrev(
    filtersViewModel: SectionViewModel = viewModel()
) {
    FiltersSection(
        modifier = Modifier,
        selectedDepartment = filtersViewModel.department.value,
        selectedTeacher = filtersViewModel.teacher.value,
        onSelectedDepartmentChange = {
                newValue -> filtersViewModel.changeCurrentDepartment(newValue)
        },
        onSelectedTeacherChange = {
                newValue -> filtersViewModel.changeCurrentTeacher(newValue)
        },
        onSelectedDateIntervalChange = {
                newValue -> filtersViewModel.changeDateInterval(newValue)
        },
        departments = filtersViewModel.departments.value?.toTypedArray() ?: emptyArray(),
        teachers  = filtersViewModel.teachers.toTypedArray(),
        datesIntervals = filtersViewModel.dateIntervals
    )
}