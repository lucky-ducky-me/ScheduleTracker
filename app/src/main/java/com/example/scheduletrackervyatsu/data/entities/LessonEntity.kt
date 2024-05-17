package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

@Entity(
    tableName = "lesson",
    foreignKeys = [
        ForeignKey(
            entity = TeacherEntity::class,
            parentColumns = ["teacherId"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DepartmentEntity::class,
            parentColumns = ["departmentId"],
            childColumns = ["departmentId"],
            onDelete = ForeignKey.CASCADE
        )
        ,
        ForeignKey(
            entity = LessonStatusEntity::class,
            parentColumns = ["lessonStatusId"],
            childColumns = ["lessonStatusId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["date", "time", "departmentId", "teacherId"],
            unique = true)
    ]
)
class LessonEntity(
    @PrimaryKey()
    @ColumnInfo(name = "lessonId")
    val lessonId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "teacherId")
    val teacherId: String,

    @ColumnInfo(name = "departmentId")
    val departmentId: String,

    @ColumnInfo(name = "data")
    val data: String,

    @ColumnInfo(name = "oldData")
    val oldData: String?,

    @ColumnInfo(name = "office")
    val office: String?,

    @ColumnInfo(name = "oldOffice")
    val oldOffice: String?,

    @ColumnInfo(name = "lessonStatusId")
    val lessonStatusId: Int,

    @ColumnInfo(name = "week")
    val week: Boolean,

    @ColumnInfo(name = "dayOfWeek")
    val dayOfWeek: String,

    @ColumnInfo(name = "isStatusWatched")
    val isStatusWatched: Boolean,

    @ColumnInfo(name = "modifiedOn")
    val modifiedOn: String = ""
)

data class TeacherWithLessons(
    @Embedded val teacher: TeacherEntity?,
    @Relation(
        parentColumn = "teacherId",
        entityColumn = "teacherId"
    )
    val lessons: List<LessonEntity>
)

data class LessonStatusesWithLessons(
    @Embedded val lessonStatus: LessonStatusEntity?,
    @Relation(
        parentColumn = "lessonStatusId",
        entityColumn = "lessonStatusId"
    )
    val lessons: List<LessonEntity>
)

data class DepartmentWithLessons(
    @Embedded val teacher: DepartmentEntity?,
    @Relation(
        parentColumn = "departmentId",
        entityColumn = "departmentId"
    )
    val lessons: List<LessonEntity>
)