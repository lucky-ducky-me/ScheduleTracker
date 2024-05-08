package com.example.scheduletrackervyatsu.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.ui.uiData.FiltersSectionData

@Composable
fun ChangesSection(
    modifier: Modifier = Modifier,
    filtersSectionData: FiltersSectionData
) {
    LazyColumn (
        modifier = modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = true
    ) {
        item {
            FiltersSection(
                modifier = Modifier,
                selectedDepartment = filtersSectionData.department,
                selectedTeacher =  filtersSectionData.teacher,
                selectedDateTimeInterval = filtersSectionData.datetimeInterval,
                onSelectedDepartmentChange = filtersSectionData.onSelectedDepartmentChange,
                onSelectedTeacherChange = filtersSectionData.onSelectedTeacherChange,
                onSelectedDateIntervalChange = filtersSectionData.onSelectedDateTimeIntervalChange,
                departments = filtersSectionData.departments.toTypedArray(),
                teachers  = filtersSectionData.teachers.toTypedArray(),
                datesIntervals = filtersSectionData.datetimeIntervals.toTypedArray(),
            )
        }

    }
}
