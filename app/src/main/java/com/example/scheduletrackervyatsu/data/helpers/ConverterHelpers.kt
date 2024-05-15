package com.example.scheduletrackervyatsu.data.helpers

import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.parsing_models.LessonParsingModel
import java.time.LocalDateTime


/**
 * Получение инициалов.
 */
fun getInitials(name: String, surname: String, patronymic: String?): String {
    return (surname + " "+
            "${name[0]}."+
            if (patronymic != null)
                "${patronymic[0]}."
            else ""
            ).trim()
}

/**
 * Преобразование занятия из модели для парсинга в сущность БД.
 */
fun fromLessonParsingModelsToEntity(
    lessonParsingModel: LessonParsingModel,
    departmentId: String,
    teacherId: String) : LessonEntity  {
    val date = lessonParsingModel.date
    val time = lessonParsingModel.time

    val combinedDateTime: LocalDateTime = LocalDateTime.of(date, time)

    return LessonEntity(
        name = lessonParsingModel.name,
        departmentId = departmentId,
        dateTime = combinedDateTime.toString(),
        teacherId = teacherId
    )
}
