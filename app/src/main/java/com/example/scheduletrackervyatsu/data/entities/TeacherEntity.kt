package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "teacher")
data class TeacherEntity(
    @PrimaryKey()
    @ColumnInfo(name = "teacherId")
    var teacherId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "surname") var surname: String,
    @ColumnInfo(name = "patronymic") var patronymic: String?,

    @ColumnInfo(name = "fio")
    var fio: String = surname + " " + name + if (patronymic != null) " $patronymic" else "",

    @ColumnInfo(name = "defaultDepartment")
    var defaultDepartment: String? = null
)
