package com.example.scheduletrackervyatsu.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.DepartmentWithTeachers
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.LessonStatusEntity
import com.example.scheduletrackervyatsu.data.entities.Logs
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherWithDepartments
import com.example.scheduletrackervyatsu.data.entities.TeachersDepartmentCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleTrackerDao {
    // Добавление преподавателя.
    @Insert
    fun insert(entity: TeacherEntity)

    // Добавление кафедры.
    @Insert
    fun insert(entity: DepartmentEntity)

    // Добавление занятия.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: LessonEntity)

    // Добавление отслеживание преподавателя.
    @Insert
    fun insert(entity: TeachersDepartmentCrossRef)

    // Добавление статуса изменения занятия.
    @Insert
    fun insert(entity: LessonStatusEntity)

    // Добавление лога.
    @Insert
    fun insert(entity: Logs)

    // Удаление одного отслеживания.
    @Delete
    fun delete(entity: TeachersDepartmentCrossRef)

    // Удаление всех отслеживаний для выбранного преподавателя.
    @Query("DELETE FROM teachersDepartmentCrossRef WHERE teacherId = :teacherId")
    fun deleteAllTrackingForTeacher(teacherId: String)

    // Удаление занятий выбранного преподавателя.
    @Query("DELETE FROM lesson WHERE teacherId = :teacherId")
    fun deleteLessons(teacherId: String)

    // Удаление занятий выбранного преподавателя с выбранной кафедры.
    @Query("DELETE FROM lesson WHERE teacherId = :teacherId AND departmentId = :departmentId")
    fun deleteLessons(teacherId: String, departmentId: String)

    // Удаление занятий выбранного преподавателя с выбранной кафедры.
    @Transaction
    fun deleteTeacherData(teacherId: String, departmentId: String? = null) {
        if (departmentId == null) {
            deleteAllTrackingForTeacher(teacherId)
            deleteLessons(teacherId)
        }
        else {
            delete(TeachersDepartmentCrossRef(teacherId= teacherId, departmentId = departmentId))
            deleteLessons(teacherId, departmentId)
        }
    }

    // Удаление занятий до указанной даты.
    @Query("DELETE FROM lesson WHERE date < :date")
    fun deletePreviousSchedule(date: String)

    // Удаление логов до указанной даты.
    @Query("DELETE FROM lesson WHERE date < :date")
    fun deleteLogs(date: String)

    // Получение преподавателя.
    @Query("SELECT * FROM teacher WHERE teacher.teacherId = :teacherId")
    fun getTeacher(teacherId: String): TeacherEntity

    // Получение кафедры.
    @Query("SELECT * FROM department WHERE department.departmentId = :departmentId")
    fun getDepartment(departmentId: String): DepartmentEntity

    // Получение всех преподавателей.
    @Query("SELECT * FROM teacher")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    // Получение всех кафедры.
    @Query("SELECT * FROM department")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    // Получение всех занятий.
    @Query("SELECT * FROM lesson")
    fun getAllLessons(): List<LessonEntity>

    @Transaction
    @Query("SELECT * FROM teacher")
    fun getTeacherWithDepartments(): List<TeacherWithDepartments>

    @Transaction
    @Query("SELECT * FROM department")
    fun getDepartmentWithTeachers(): List<DepartmentWithTeachers>

    // Получение всех отслеживаний для преподавателей.
    @Query("SELECT * FROM teacher " +
            "INNER JOIN teachersDepartmentCrossRef " +
            "   ON teacher.teacherId = teachersDepartmentCrossRef.teacherId " +
            "INNER JOIN department " +
            "   ON teachersDepartmentCrossRef.departmentId = department.departmentId")
    fun getTrackedTeachersDepartments(): Flow<Map<TeacherEntity, List<DepartmentEntity>>>

    // Получение отслеживаемых преподавателей.
    @Query("SELECT * FROM teacher " +
            "INNER JOIN teachersDepartmentCrossRef " +
            "   ON teacher.teacherId = teachersDepartmentCrossRef.teacherId " +
            "   GROUP BY teacher.teacherId")
    fun getTrackingTeachers(): Flow<List<TeacherEntity>>

    // Получение всех отслеживаемых кафедр у преподавателя.
    @Query("SELECT * FROM department " +
            "INNER JOIN teachersDepartmentCrossRef " +
            "   ON department.departmentId = teachersDepartmentCrossRef.departmentId " +
            "   AND teachersDepartmentCrossRef.teacherId = :teacherId")
    fun getTrackingTeacherDepartments(teacherId: String): Flow<List<DepartmentEntity>>

    // Получение всех отслеживаний для преподавателей.
    @Query("SELECT * FROM teacher " +
            "INNER JOIN teachersDepartmentCrossRef " +
            "   ON teacher.teacherId = teachersDepartmentCrossRef.teacherId " +
            "INNER JOIN department " +
            "   ON teachersDepartmentCrossRef.departmentId = department.departmentId")
    fun getTrackedTeachersDepartmentsNowFlow(): Map<TeacherEntity, List<DepartmentEntity>>

    // Получение всех занятий у выбранного преподавателя.
    @Query("SELECT * FROM lesson " +
            "WHERE " +
            " lesson.teacherId = :teacherId " +
            "ORDER BY lesson.date, lesson.time")
    fun getLessons(teacherId: String): List<LessonEntity>

    // Получение всех занятий у выбранного преподавателя.
    @Query("SELECT * FROM lesson " +
            "WHERE " +
            " lesson.teacherId = :teacherId " +
            "ORDER BY lesson.date, lesson.time")
    fun getLessonsFlow(teacherId: String): Flow<List<LessonEntity>>

    // Получение занятий в указанный день.
    @Query("SELECT * FROM lesson WHERE lesson.date = :date")
    fun getLessonsByDay(date:String): List<LessonEntity>

    // Получение конкретного занятия.
    @Query("SELECT * FROM lesson WHERE lesson.date = :date " +
            "AND lesson.time = :time " +
            "AND lesson.teacherId = :teacherId " +
            "AND lesson.departmentId = :departmentId")
    fun getLesson(teacherId: String, departmentId: String, date: String, time: String): LessonEntity

    // Получение занятия по его Id.
    @Query("SELECT * FROM lesson WHERE lessonId = :lessonId")
    fun getLesson(lessonId: String): Flow<LessonEntity>

    // Смена статус просмотра изменения занятия.
    @Query("UPDATE lesson SET isStatusWatched = :isWatched WHERE lessonId = :lessonId")
    fun changeLessonStatusVisibility(lessonId: String, isWatched: Boolean)

    // Смена статус просмотра изменения всех занятий.
    @Query("UPDATE lesson SET isStatusWatched = :isWatched WHERE teacherId = :teacherId")
    fun changeAllLessonsStatusVisibility(teacherId: String, isWatched: Boolean)

    // Получение занятия с непросмотренным статусом изменения.
    @Query("SELECT * FROM lesson WHERE teacherId = :teacherId " +
            "AND lessonStatusId != 1 AND isStatusWatched = 0 ORDER BY modifiedOn")
    fun getNotWatchLessonsFlow(teacherId: String): Flow<List<LessonEntity>>

    // Получение занятия с непросмотренным статусом изменения.
    @Query("SELECT * FROM lesson WHERE teacherId = :teacherId " +
            "AND lessonStatusId != 1 AND isStatusWatched = 0 ORDER BY modifiedOn")
    fun getNotWatchLessons(teacherId: String): List<LessonEntity>

    // Получение отслеживание преподавателя.
    @Query("SELECT * FROM teachersDepartmentCrossRef" +
            " WHERE teacherId = :teacherId AND departmentId = :departmentId")
    fun getTeachersDepartmentCrossRef(teacherId: String, departmentId: String): TeachersDepartmentCrossRef
}