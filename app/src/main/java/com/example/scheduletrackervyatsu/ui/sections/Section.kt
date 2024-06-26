package com.example.scheduletrackervyatsu.ui.sections

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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

/**
 * Основная страница.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Section(
    modifier: Modifier = Modifier,
    sectionViewModel: SectionViewModel = viewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    var currentDestination = currentBackStack?.destination

    val trackingTeachers = sectionViewModel.trackingTeachers.collectAsState(initial = emptyList()).value
    val lessonsByWeeks = sectionViewModel.lessonsByWeeks.collectAsState(initial = emptyList()).value

    val teacher = sectionViewModel.teacher.collectAsState(initial = null).value

    val lessonsNotWatched = sectionViewModel.lessonsNotWatched.collectAsState(initial = emptyList()).value

    val currentPage = sectionViewModel.currentPage.intValue

    val currentTab = sectionViewModel.tabRowDirectionState

    val filtersSectionData = FiltersSectionData(
        teacher = teacher,
        onSelectedTeacherChange = { newValue ->
            sectionViewModel.changeCurrentTeacher(
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

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        bottomBar = {
            TabRow(
                onTabClick = {
                    sectionViewModel.changeTabRowDirection(it)
                    when (it) {
                        TabRowDirection.Schedule -> navController.navigateSingleTopTo("Schedule")
                        TabRowDirection.Changes -> navController.navigateSingleTopTo("Changes")
                        TabRowDirection.Settings -> navController.navigateSingleTopTo("Settings")
                        TabRowDirection.Info -> openInfoDialog = true
                    }
                },
                tabRowDirection = currentTab.value
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
                        sectionViewModel.getLessons()
                        if (filtersSectionData.teacher != null) {
                            Toast.makeText(context, "Расписание загружено", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else {
                            Toast.makeText(context, "Выберите преподавателя", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    lessonsByWeeks = lessonsByWeeks,
                    onWatchChangeClick = {
                        lessonId: String ->
                        sectionViewModel.watchLessonStatus(lessonId)
                        Toast.makeText(context, "Изменение просмотрено", Toast.LENGTH_SHORT).show()
                    },
                    onChangeLessonClick = {
                        lessonId -> navController.navigateSingleTopTo("Schedule/$lessonId")
                    },
                    onTest = {
                        sectionViewModel.testButton()
                    },
                    currentPage = currentPage,
                    onCurrentPageNext = {
                        sectionViewModel.addValueToCurrentPage(1)
                    },
                    onCurrentPagePrevious = {
                        sectionViewModel.addValueToCurrentPage(-1)
                    },
                )
            }
            composable(route = "Changes") {
                ChangesSection(
                    filtersSectionData = filtersSectionData,
                    onAcceptButtonClick = {
                        sectionViewModel.getLessons()
                        if (filtersSectionData.teacher != null) {
                            Toast.makeText(context, "Изменения загружены", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else {
                            Toast.makeText(context, "Выберите преподавателя", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    onWatchChangeClick = {
                            lessonId: String -> sectionViewModel.watchLessonStatus(lessonId)
                        Toast.makeText(context, "Изменение просмотрено", Toast.LENGTH_SHORT).show()
                    },
                    lessonsNotWatched = lessonsNotWatched,
                    onWatchAllChangesButtonClick = {
                        sectionViewModel.watchAllLessonsStatus(it)
                        if (filtersSectionData.teacher != null) {
                            Toast.makeText(context, "Изменения просмотрены", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(context, "Выберите преподавателя", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
            }
            composable(route = "Settings") {
                SettingsSection(onTest = {
                    sectionViewModel.testButton()
                },)
            }
            composable(route = "Schedule/{lessonId}",
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })) {
                    navBackStackEntry ->
                val lessonId = navBackStackEntry.arguments?.getString("lessonId")
                sectionViewModel.getLesson(lessonId)

                SingleLessonSection(lessonEntity = sectionViewModel.watchingLesson.collectAsState(
                    initial = null
                ).value, onWatchChangeClick = {
                    lessonId: String ->
                    navController.navigateSingleTopTo("Schedule")
                    sectionViewModel.watchLessonStatus(lessonId)
                })
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }