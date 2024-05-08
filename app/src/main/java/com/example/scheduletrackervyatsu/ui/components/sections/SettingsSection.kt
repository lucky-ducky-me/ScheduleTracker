package com.example.scheduletrackervyatsu.ui.components.sections

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
) {
    val tableData = remember {
        mutableStateListOf("Преподаватель 1", "Преподаватель 2")
    }

    var openAlertDialog2 by remember { mutableStateOf(false) }

    if (openAlertDialog2) {
        AlertDialogExample(
            onDismissRequest = { openAlertDialog2 = false },
            onConfirmation = {
                openAlertDialog2 = false
                tableData.add(it)
                println("Confirmation registered") // Add logic here to handle confirmation.
            },
            dialogTitle = "Alert dialog example",
            dialogText = "This is an example of an alert dialog with buttons.",
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            item {
                Row(
                    modifier = modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = modifier.weight(0.9f),
                        text = "Список преподавателей",
                        textAlign = TextAlign.Center)
                    IconButton(onClick = { openAlertDialog2 = !openAlertDialog2 }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }


            }

            items(tableData) {
                var list = remember {
                    mutableStateListOf("dep1", "dep2")
                }

                var openAlertDialog by remember { mutableStateOf(false) }

                if (openAlertDialog) {
                    AlertDialogExample(
                        onDismissRequest = { openAlertDialog = false },
                        onConfirmation = {
                            openAlertDialog = false
                            list.add(it)
                            println("Confirmation registered") // Add logic here to handle confirmation.
                        },
                        dialogTitle = "Alert dialog example",
                        dialogText = "This is an example of an alert dialog with buttons.",
                    )
                }

                Column(
                    modifier = modifier.fillMaxWidth()
                ) {
                    var expanded by remember {
                        mutableStateOf(false)
                    }

                    Row(modifier = modifier.fillMaxWidth()) {
                        TextField(
                            modifier = modifier
                                .border(width = 1.dp, color = Color.Black)
                                .fillMaxWidth(),
                            onValueChange = {},
                            readOnly = true,
                            value = it,
                            trailingIcon = {
                                Row() {
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            imageVector =
                                            if (expanded)
                                                Icons.Default.ArrowDropUp
                                            else
                                                Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                        )
                    }

                    if (expanded) {
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
                                IconButton(onClick = { openAlertDialog = !openAlertDialog  }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            }
                            list.map {
                                Row(modifier = modifier.wrapContentHeight(Alignment.CenterVertically),
                                ) {
                                    Text(
                                        modifier = Modifier.weight(0.9f),
                                        text = it,
                                        textAlign = TextAlign.Left,
                                    )
                                    IconButton(
                                        modifier = Modifier.weight(0.1f),
                                        onClick = {
                                            list.remove(it)
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
                }
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    var departmentName by remember {
        mutableStateOf("")
    }

    AlertDialog(
        icon = {
            //Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
            Text(text = dialogTitle)
            Text(text = dialogTitle)
        },
        text = {
            TextField(
                modifier = Modifier
                    .border(width = 1.dp, color = Color.Black)
                    .fillMaxWidth(),
                onValueChange = {
                    departmentName = it
                },
                value = departmentName,
                trailingIcon = {})
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(departmentName)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}