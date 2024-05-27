package com.example.scheduletrackervyatsu.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Сущность БД для логов.
 */
@Entity(
    tableName = "log",
)
data class Logs(
    /**
     * Идентификатор лога.
     */
    @PrimaryKey
    @ColumnInfo(name = "logId")
    val logId: String = UUID.randomUUID().toString(),

    /**
     * Дата и время лога.
     */
    @ColumnInfo(name = "dateTime")
    val dateTime: String,

    /**
     * Текст лога.
     */
    @ColumnInfo(name = "text")
    val text: String,
)