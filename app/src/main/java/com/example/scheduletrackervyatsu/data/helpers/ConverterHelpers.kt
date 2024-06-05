package com.example.scheduletrackervyatsu.data.helpers

import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
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
            else "."
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
        lessonStatusId = 1,
        office = office,
        oldData = null,
        oldOffice = null,
        week = lessonParsingModel.week,
        isStatusWatched = true,
        modifiedOn = LocalDateTime.now().toString()
    )
}

/**
 * Преобразование списка моделей занятия в сущность БД.
 */
fun fromLessonParsingModelsToEntities(
    lessons: List<LessonParsingModel>,
    departments: List<DepartmentEntity>,
    teacherId: String): List<LessonEntity>
{
    return lessons.mapNotNull {
        val department = departments.find {
                department -> department.name == it.department
        }

        if (department != null) {
            fromLessonParsingModelsToEntity(it, department.departmentId, teacherId)
        }
        else
            null
    }
}
