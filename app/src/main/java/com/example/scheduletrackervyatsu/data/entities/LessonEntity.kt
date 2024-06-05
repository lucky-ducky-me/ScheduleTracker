package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

/**
 * Сущность БД для занятия.
 */
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
    /**
     * Id.
     */
    @PrimaryKey()
    @ColumnInfo(name = "lessonId")
    val lessonId: String = UUID.randomUUID().toString(),

    /**
     * Дата.
     */
    @ColumnInfo(name = "date")
    val date: String,

    /**
     * Время.
     */
    @ColumnInfo(name = "time")
    val time: String,

    /**
     * Id преподавателя.
     */
    @ColumnInfo(name = "teacherId")
    val teacherId: String,

    /**
     * Id кафедры.
     */
    @ColumnInfo(name = "departmentId")
    val departmentId: String,

    /**
     * Данные занятия.
     */
    @ColumnInfo(name = "data")
    val data: String,

    /**
     * Старые данные занятия.
     */
    @ColumnInfo(name = "oldData")
    val oldData: String?,

    /**
     * Кабинет.
     */
    @ColumnInfo(name = "office")
    val office: String?,

    /**
     * Старый кабинет.
     */
    @ColumnInfo(name = "oldOffice")
    val oldOffice: String?,

    /**
     * Статус занятия.
     */
    @ColumnInfo(name = "lessonStatusId")
    val lessonStatusId: Int,

    /**
     * Учебная неделя.
     */
    @ColumnInfo(name = "week")
    val week: Boolean,

    /**
     * Просмотрен ли статус.
     */
    @ColumnInfo(name = "isStatusWatched")
    val isStatusWatched: Boolean,

    /**
     * Дата изменения.
     */
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