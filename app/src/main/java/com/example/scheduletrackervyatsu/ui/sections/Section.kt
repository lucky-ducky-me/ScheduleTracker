package com.example.scheduletrackervyatsu.ui.sections

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.scheduletrackervyatsu.domain.SectionViewModel
import com.example.scheduletrackervyatsu.ui.components.InfoDialog
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
    val lessonsByWeeks = filtersViewModel.lessonsByWeeks.collectAsState(initial = emptyList()).value

    val teacher = filtersViewModel.teacher.collectAsState(initial = null).value

    val lessonsNotWatched = filtersViewModel.lessonsNotWatched.collectAsState(initial = emptyList()).value

    val currentPage = filtersViewModel.currentPage.intValue

    val filtersSectionData = FiltersSectionData(
        teacher = teacher,
        onSelectedTeacherChange = { newValue ->
            filtersViewModel.changeCurrentTeacher(
                newValue
            )
        },
        trackingTeachers = trackingTeachers
    )

    var openInfoDialog by remember { mutableStateOf(false) }

    if (openInfoDialog) {
        InfoDialog(
            onConfirmation = {
                openInfoDialog = false
            },
            dialogTitle = "Инструкция:",
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            TabRow(
                onTabClick = {
                    when (it) {
                        TabRowDirection.Schedule -> navController.navigateSingleTopTo("Schedule")
                        TabRowDirection.Changes -> navController.navigateSingleTopTo("Changes")
                        TabRowDirection.Settings -> navController.navigateSingleTopTo("Settings")
                        TabRowDirection.Info -> openInfoDialog = true
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
                    lessonsByWeeks = lessonsByWeeks,
                    onWatchChangeClick = {
                        lessonId: String -> filtersViewModel.watchLessonStatus(lessonId)
                    },
                    onChangeLessonClick = {
                        lessonId -> navController.navigateSingleTopTo("Schedule/$lessonId")
                    },
                    onTest = {
                        filtersViewModel.testButton()
                    },
                    currentPage = currentPage,
                    onCurrentPageNext = {
                        filtersViewModel.addValueToCurrentPage(1)
                    },
                    onCurrentPagePrevious = {
                        filtersViewModel.addValueToCurrentPage(-1)
                    },
                    onCheckScheduleOnChanges = {
                        filtersViewModel.runStandardCheck()
                    }
                )
            }
            composable(route = "Changes") {
                ChangesSection(
                    filtersSectionData = filtersSectionData,
                    onAcceptButtonClick = {
                        filtersViewModel.getLessons()
                    },
                    onWatchChangeClick = {
                            lessonId: String -> filtersViewModel.watchLessonStatus(lessonId)
                    },
                    lessonsNotWatched = lessonsNotWatched
                )
            }
            composable(route = "Settings") {
                SettingsSection()
            }
            composable(route = "Schedule/{lessonId}",
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })) {
                    navBackStackEntry ->
                val lessonId = navBackStackEntry.arguments?.getString("lessonId")
                filtersViewModel.getLesson(lessonId)

                SingleLessonSection(lessonEntity = filtersViewModel.watchingLesson.collectAsState(
                    initial = null
                ).value, onWatchChangeClick = {
                    lessonId: String ->
                    navController.navigateSingleTopTo("Schedule")
                    filtersViewModel.watchLessonStatus(lessonId)
                })
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }