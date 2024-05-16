package com.example.scheduletrackervyatsu.data.helpers

import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.parsing_models.LessonParsingModel


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

    val office = if (lessonParsingModel.name.isNotEmpty()
        && lessonParsingModel.name[0].isDigit())
        lessonParsingModel.name.substring(0, lessonParsingModel.name.indexOf(" "))
    else
        null

    val nameWithoutOffice = if (lessonParsingModel.name.isNotEmpty()
        && lessonParsingModel.name[0].isDigit())
        lessonParsingModel.name.substring(lessonParsingModel.name.indexOf(" "))
    else
        lessonParsingModel.name

    return LessonEntity(
        data = nameWithoutOffice,
        departmentId = departmentId,
        date = lessonParsingModel.date.toString(),
        time = lessonParsingModel.time.toString(),
        teacherId = teacherId,
        lessonStatusId = 0,
        office = office,
        oldData = "",
        oldOffice = "",
        week = lessonParsingModel.week,
        dayOfWeek = lessonParsingModel.dayOfWeek
    )
}
