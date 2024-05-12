package com.example.scheduletrackervyatsu.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity

@Composable
fun DepartmentSettingItem(
    modifier: Modifier = Modifier,
    setting: Pair<TeacherEntity, List<DepartmentEntity>>,
    onAddButtonClick: (String) -> Unit,
    onDeleteButtonClick: (String) -> Unit
) {
    Column(
        modifier  = Modifier
            .border(width = 1.dp, color = Color.Black)
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row (
            modifier  = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(modifier  = Modifier, text = "Кафедры")
            IconButton(onClick = {
                onAddButtonClick(setting.first.teacherId)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
        setting.second.map {
            Row(modifier = modifier.wrapContentHeight(Alignment.CenterVertically),
            ) {
                Text(
                    modifier = Modifier.weight(0.9f),
                    text = it.name,
                    textAlign = TextAlign.Left,
                )
                IconButton(
                    modifier = Modifier.weight(0.1f),
                    onClick = {
                        onDeleteButtonClick(it.departmentId)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
            }
        }
    }
}