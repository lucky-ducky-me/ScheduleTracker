package com.example.scheduletrackervyatsu.ui.components.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.scheduletrackervyatsu.DateIntervals
import com.example.scheduletrackervyatsu.domain.SectionViewModel
import com.example.scheduletrackervyatsu.ui.components.TabRow
import com.example.scheduletrackervyatsu.ui.components.TabRowDirection
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
    var currentDestination = currentBackStack?.destination

    val trackingTeachers = filtersViewModel.trackingTeachers.collectAsState(initial = emptyList()).value
    val trackingTeacherDepartments = filtersViewModel.trackingTeacherDepartments.collectAsState(initial = emptyList()).value
    val dateIntervals = filtersViewModel.dateIntervals
    val lessons = filtersViewModel.lessons.collectAsState(initial = emptyList()).value
    val lessonsByWeeks = filtersViewModel.lessonsByWeeks.collectAsState(initial = emptyList()).value

    val teacher = filtersViewModel.teacher.collectAsState(initial = null).value
    val department = filtersViewModel.department.collectAsState(initial = null).value

    val filtersSectionData = FiltersSectionData(
        department = department,
        teacher = teacher,
        datetimeInterval = filtersViewModel.dateInterval.value,
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
                dateIntervals.toList().find { it.second == newValue }?.first
                    ?: DateIntervals.Week
            )
        },
        departments = trackingTeacherDepartments,
        teachers = trackingTeachers,
        datetimeIntervals = dateIntervals,
        trackingTeachers = trackingTeachers
    )

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
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable(route = "Schedule") {
                ScheduleSection(
                    filtersSectionData = filtersSectionData,
                    onAcceptButtonClick = {
                        filtersViewModel.getLessons()
                    },
                    lessonsByWeeks = lessonsByWeeks
                )
            }
            composable(route = "Changes") {
                ChangesSection(
                    filtersSectionData = filtersSectionData,
                    onAcceptButtonClick = {
                        filtersViewModel.getLessons()
                    },
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