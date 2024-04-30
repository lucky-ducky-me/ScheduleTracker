package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "changeStatus"
)
data class ChangeStatusEntity(
    @PrimaryKey()
    @ColumnInfo(name = "changeStatusId")
    val changeStatusId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name") val name: String?,
)
