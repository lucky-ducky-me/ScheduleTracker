package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность БД для статуса занятия.
 */
@Entity(
    tableName = "lessonStatus"
)
data class LessonStatusEntity(
    /**
     * Id.
     */
    @PrimaryKey()
    @ColumnInfo(name = "lessonStatusId")
    val lessonStatusId: Int,

    /**
     * Название.
     */
    @ColumnInfo(name = "name")
    val name: String,
)
