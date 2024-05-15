package com.example.scheduletrackervyatsu.ui.components.sections

import android.util.Log
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
import com.example.scheduletrackervyatsu.DateIntervals
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.domain.SectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSection(
    modifier: Modifier = Modifier,
    selectedDepartment: DepartmentEntity?,
    selectedTeacher: TeacherEntity?,
    selectedDateTimeInterval: DateIntervals,
    onSelectedDepartmentChange: (String) -> Unit,
    onSelectedTeacherChange: (String) -> Unit,
    onSelectedDateIntervalChange: (String) -> Unit,
    departments: List<DepartmentEntity>,
    teachers: List<TeacherEntity>,
    datesIntervals: Map<DateIntervals, String>,
    onAcceptButtonClick: () -> Unit
) {
    val context = LocalContext.current

    var expandedDateInterval by remember { mutableStateOf(false) }
    var expandedTeacher by remember { mutableStateOf(false) }
    var expandedDepartment by remember { mutableStateOf(false) }

    var selectedTeacherState by rememberSaveable {
        mutableStateOf(selectedTeacher?.fio ?: "")
    }

     val teachersFIO = teachers.map { it.fio }

    var dateIntervalName by remember {
       mutableStateOf(datesIntervals[selectedDateTimeInterval] ?: "")
   }

    dateIntervalName = datesIntervals[selectedDateTimeInterval] ?: ""

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
                value = selectedTeacher?.fio ?: "",
                onValueChange = {
                    selectedTeacherState = it
                },
                label = { Text(text = "Выберите преподавателя") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTeacher)
               },
                modifier = Modifier.menuAnchor(),

            )

            val filteredOptions = teachersFIO.filter { selectedTeacherState.contains(selectedTeacherState, ignoreCase = true) }

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
                                onSelectedTeacherChange(teachers.find {
                                    it.fio.contains(selectedTeacherState) }?.teacherId ?: "")
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
                value = selectedDepartment?.name ?: "",
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
                                text = item.name,
                            )
                        },
                        onClick = {
                            expandedDepartment = false
                            Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                            //todo убрать потом колбек надо
                            onSelectedDepartmentChange(item.departmentId)
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
                value = dateIntervalName,
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
                datesIntervals.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = value,
                            )
                        },
                        onClick = {
                            expandedDateInterval = false
                            dateIntervalName = value
                            Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
                            onSelectedDateIntervalChange(dateIntervalName)
                        }
                    )
                }
            }
        }


        Button(onClick = {
            onAcceptButtonClick()
        }) {
            Text("Применить")
        }
    }
}