package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scheduletrackervyatsu.domain.SectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Section(
    modifier: Modifier = Modifier,
    title: String,
    filtersViewModel: SectionViewModel = viewModel()
) {


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val array = filtersViewModel.departments.collectAsState().value

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = true
        ) {
            item {
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
                    departments = filtersViewModel.departments.value.toTypedArray() ?: emptyArray(),
                    teachers  = filtersViewModel.teachers.toTypedArray(),
                    datesIntervals = filtersViewModel.dateIntervals
                )
            }
            item {
                Day(Modifier, "Понедельник")
            }
        }
    }
}