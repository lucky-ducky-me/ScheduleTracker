package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import java.util.UUID

@Entity(
    tableName = "teachersDepartmentCrossRef",
    primaryKeys = ["teacherId", "departmentId"],
    foreignKeys = [
        ForeignKey(
            entity = TeacherEntity::class,
            parentColumns = ["teacherId"],
            childColumns = ["teacherId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DepartmentEntity::class,
            parentColumns = ["departmentId"],
            childColumns = ["departmentId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ])
data class TeachersDepartmentCrossRef(
    @ColumnInfo(name = "teacherId")
    val teacherId: String,

    @ColumnInfo(name = "departmentId")
    val departmentId: String
)

data class TeacherWithDepartments(
    @Embedded val teacher: TeacherEntity,
    @Relation(
        parentColumn = "teacherId",
        entityColumn = "departmentId",
        associateBy = Junction(TeachersDepartmentCrossRef::class)
    )
    val departments: List<DepartmentEntity>?
)

data class DepartmentWithTeachers(
    @Embedded val department: DepartmentEntity,
    @Relation(
        parentColumn = "departmentId",
        entityColumn = "teacherId",
        associateBy = Junction(TeachersDepartmentCrossRef::class)
    )
    val teachers: List<TeacherEntity>?
)