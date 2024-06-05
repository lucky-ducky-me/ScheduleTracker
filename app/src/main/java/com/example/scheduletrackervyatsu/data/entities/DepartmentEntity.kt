package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Сущность БД для кафедры.
 */
@Entity(
    tableName = "department",
    indices = [Index("name", unique = true)]
)
data class DepartmentEntity(
    /**
     * Id.
     */
    @PrimaryKey
    @ColumnInfo(name = "departmentId")
    val departmentId: String = UUID.randomUUID().toString(),

    /**
     * Название.
     */
    @ColumnInfo(name = "name") val name: String,
)