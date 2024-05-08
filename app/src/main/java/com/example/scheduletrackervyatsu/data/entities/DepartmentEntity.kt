package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "department",
    indices = [Index("name", unique = true)]
)
data class DepartmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "departmentId")
    val departmentId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name") val name: String,
)