package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "teacher")
data class TeacherEntity(
    @PrimaryKey()
    @ColumnInfo(name = "teacherId")
    val teacherId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "surname") val surname: String,
    @ColumnInfo(name = "patronymic") val patronymic: String?,

    @ColumnInfo(name = "fio")
    val fio: String = surname + " " + surname + if (patronymic != null) " $patronymic" else "",
)
