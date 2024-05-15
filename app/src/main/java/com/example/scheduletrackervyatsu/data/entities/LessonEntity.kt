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
    ],
    indices = [
        Index(value = ["dateTime", "departmentId", "teacherId"], unique = true)
    ]
)
class LessonEntity(
    @PrimaryKey()
    @ColumnInfo(name = "lessonId")
    val lessonId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "dateTime")
    val dateTime: String,

    @ColumnInfo(name = "teacherId")
    val teacherId: String,

    @ColumnInfo(name = "departmentId")
    val departmentId: String,

    @ColumnInfo(name = "name")
    val name: String
)

data class TeacherWithLessons(
    @Embedded val teacher: TeacherEntity?,
    @Relation(
        parentColumn = "teacherId",
        entityColumn = "teacherId"
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