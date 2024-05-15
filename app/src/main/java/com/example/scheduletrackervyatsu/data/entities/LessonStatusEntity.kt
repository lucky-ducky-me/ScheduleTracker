package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessonStatus"
)
data class LessonStatusEntity(
    @PrimaryKey()
    @ColumnInfo(name = "lessonStatusId")
    val lessonStatusId: Int,

    @ColumnInfo(name = "name")
    val name: String,
)
