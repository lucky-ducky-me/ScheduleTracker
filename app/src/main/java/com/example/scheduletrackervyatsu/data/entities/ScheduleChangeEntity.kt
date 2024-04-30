package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

@Entity(
    tableName = "scheduleChange",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["lessonId"],
            childColumns = ["lessonId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChangeStatusEntity::class,
            parentColumns = ["changeStatusId"],
            childColumns = ["changeStatusId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ])
data class ScheduleChangeEntity(
    @PrimaryKey()
    @ColumnInfo(name = "scheduleChangeId")
    val scheduleChangeId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "modifiedOn")
    val modifiedOn: String?,

    @ColumnInfo(name = "dateTime")
    val dateTime: String?,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "lessonId")
    val lessonId: String?,

    @ColumnInfo(name = "changeStatusId")
    val changeStatusId: String?
)

data class LessonWithScheduleChanges(
    @Embedded val lesson: LessonEntity,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "lessonId"
    )
    val scheduleChanges: List<ScheduleChangeEntity>?
)
