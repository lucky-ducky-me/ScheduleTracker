package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.scheduletrackervyatsu.domain.SectionViewModel
import com.example.scheduletrackervyatsu.ui.uiData.FiltersSectionData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Section(
    modifier: Modifier = Modifier,
    title: String,
    filtersViewModel: SectionViewModel = viewModel()
) {


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination


    val departments = filtersViewModel.departments.collectAsState().value

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            TabRow(
                onTabClick = {
                    when (it) {
                        TabRowDirection.Schedule -> navController.navigateSingleTopTo("Schedule")
                        TabRowDirection.Changes -> navController.navigateSingleTopTo("Changes")
                        TabRowDirection.Settings -> navController.navigateSingleTopTo("Settings")
                    }
                }
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Schedule",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = "Schedule") {
                ScheduleSection(
                    filtersSectionData = FiltersSectionData(
                        department = filtersViewModel.department.value,
                        teacher = filtersViewModel.teacher.value,
                        datetimeInterval = filtersViewModel.datetimeInterval.value,
                        onSelectedDepartmentChange = { newValue ->
                            filtersViewModel.changeCurrentDepartment(
                                newValue
                            )
                        },
                        onSelectedTeacherChange = { newValue ->
                            filtersViewModel.changeCurrentTeacher(
                                newValue
                            )
                        },
                        onSelectedDateTimeIntervalChange = { newValue ->
                            filtersViewModel.changeDateInterval(
                                newValue
                            )
                        },
                        departments = departments,
                        teachers = filtersViewModel.teachers,
                        datetimeIntervals = filtersViewModel.dateIntervals.toList()

                    )
                )
            }
            composable(route = "Changes") {
                ChangesSection(
                    filtersSectionData = FiltersSectionData(
                        department = filtersViewModel.department.value,
                        teacher = filtersViewModel.teacher.value,
                        datetimeInterval = filtersViewModel.datetimeInterval.value,
                        onSelectedDepartmentChange = { newValue ->
                            filtersViewModel.changeCurrentDepartment(
                                newValue
                            )
                        },
                        onSelectedTeacherChange = { newValue ->
                            filtersViewModel.changeCurrentTeacher(
                                newValue
                            )
                        },
                        onSelectedDateTimeIntervalChange = { newValue ->
                            filtersViewModel.changeDateInterval(
                                newValue
                            )
                        },
                        departments = departments,
                        teachers = filtersViewModel.teachers,
                        datetimeIntervals = filtersViewModel.dateIntervals.toList()

                    )
                )
            }
            composable(route = "Settings") {
                SettingsSection()
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }