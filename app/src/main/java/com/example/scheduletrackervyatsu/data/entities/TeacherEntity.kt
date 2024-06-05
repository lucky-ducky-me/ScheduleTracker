package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Сущность БД для преподавателя.
 */
@Entity(tableName = "teacher")
data class TeacherEntity(
    /**
     * Id.
     */
    @PrimaryKey()
    @ColumnInfo(name = "teacherId")
    var teacherId: String = UUID.randomUUID().toString(),

    /**
     * Имя.
     */
    @ColumnInfo(name = "name") var name: String,

    /**
     * Фамилия.
     */
    @ColumnInfo(name = "surname") var surname: String,

    /**
     * Отчество.
     */
    @ColumnInfo(name = "patronymic") var patronymic: String?,

    /**
     * ФИО.
     */
    @ColumnInfo(name = "fio")
    var fio: String = surname + " " + name + if (patronymic != null) " $patronymic" else "",

    /**
     * Кафедра по умолчанию.
     */
    @ColumnInfo(name = "defaultDepartment")
    var defaultDepartment: String? = null,
)
