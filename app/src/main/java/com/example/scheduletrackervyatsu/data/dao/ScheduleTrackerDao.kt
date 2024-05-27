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
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherWithDepartments
import com.example.scheduletrackervyatsu.data.entities.TeachersDepartmentCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleTrackerDao {
    @Insert
    fun insert(entity: TeacherEntity)

    @Insert
    fun insert(entity: DepartmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: LessonEntity)

    @Insert
    fun insert(entity: TeachersDepartmentCrossRef)

    @Insert
    fun insert(entity: LessonStatusEntity)

    @Delete
    fun delete(entity: TeacherEntity)

    @Delete
    fun delete(entity: TeachersDepartmentCrossRef)

    @Query("DELETE FROM teachersDepartmentCrossRef WHERE teacherId = :teacherId")
    fun deleteAllTrackingForTeacher(teacherId: String)

    @Query("DELETE FROM lesson WHERE teacherId = :teacherId")
    fun deleteLessons(teacherId: String)

    @Query("DELETE FROM lesson WHERE teacherId = :teacherId AND departmentId = :departmentId")
    fun deleteLessons(teacherId: String, departmentId: String)

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

    @Query("SELECT * FROM teacher WHERE teacher.teacherId = :teacherId")
    fun getTeacher(teacherId: String): TeacherEntity

    @Query("SELECT * FROM department WHERE department.departmentId = :departmentId")
    fun getDepartment(departmentId: String): DepartmentEntity

    @Query("SELECT * FROM teacher")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM department")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    @Query("SELECT * FROM lesson")
    fun getAllLessons(): List<LessonEntity>

    @Transaction
    @Query("SELECT * FROM teacher")
    fun getTeacherWithDepartments(): List<TeacherWithDepartments>

    @Transaction
    @Query("SELECT * FROM department")
    fun getDepartmentWithTeachers(): List<DepartmentWithTeachers>

    @Query("SELECT * FROM teacher " +
            "INNER JOIN teachersDepartmentCrossRef " +
            "   ON teacher.teacherId = teachersDepartmentCrossRef.teacherId " +
            "INNER JOIN department " +
            "   ON teachersDepartmentCrossRef.departmentId = department.departmentId")
    fun getTrackedTeachersDepartments(): Flow<Map<TeacherEntity, List<DepartmentEntity>>>

    @Query("SELECT * FROM teacher " +
            "INNER JOIN teachersDepartmentCrossRef " +
            "   ON teacher.teacherId = teachersDepartmentCrossRef.teacherId " +
            "   GROUP BY teacher.teacherId")
    fun getTrackingTeachers(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM department " +
            "INNER JOIN teachersDepartmentCrossRef " +
            "   ON department.departmentId = teachersDepartmentCrossRef.departmentId " +
            "   AND teachersDepartmentCrossRef.teacherId = :teacherId")
    fun getTrackingTeacherDepartments(teacherId: String): Flow<List<DepartmentEntity>>

    @Query("SELECT * FROM teacher " +
            "INNER JOIN teachersDepartmentCrossRef " +
            "   ON teacher.teacherId = teachersDepartmentCrossRef.teacherId " +
            "INNER JOIN department " +
            "   ON teachersDepartmentCrossRef.departmentId = department.departmentId")
    fun getTrackedTeachersDepartmentsNowFlow(): Map<TeacherEntity, List<DepartmentEntity>>

    @Query("SELECT * FROM lesson " +
            "WHERE lesson.departmentId = :departmentId " +
            "   AND lesson.teacherId = :teacherId " +
            "ORDER BY lesson.date, lesson.time")
    fun getLessons(teacherId: String, departmentId: String): List<LessonEntity>

    @Query("SELECT * FROM lesson " +
            "WHERE lesson.departmentId = :departmentId " +
            "   AND lesson.teacherId = :teacherId " +
            "ORDER BY lesson.date, lesson.time")
    fun getLessonsFlow(teacherId: String, departmentId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lesson WHERE lesson.date = :date")
    fun getDayWeekAndName(date:String): List<LessonEntity>

    @Query("SELECT * FROM lesson WHERE lesson.date = :date " +
            "AND lesson.time = :time " +
            "AND lesson.teacherId = :teacherId " +
            "AND lesson.departmentId = :departmentId")
    fun getLesson(teacherId: String, departmentId: String, date: String, time: String): LessonEntity

    @Query("SELECT * FROM lesson WHERE lessonId = :lessonId")
    fun getLesson(lessonId: String): Flow<LessonEntity>

    @Query("UPDATE lesson SET isStatusWatched = :isWatched WHERE lessonId = :lessonId")
    fun changeLessonStatusVisibility(lessonId: String, isWatched: Boolean)

    @Query("DELETE FROM lesson WHERE date < :date")
    fun deletePreviousSchedule(date: String)

    @Query("SELECT * FROM lesson WHERE teacherId = :teacherId AND departmentId = :departmentId " +
            "AND lessonStatusId != 1 AND isStatusWatched = 0 ORDER BY modifiedOn")
    fun getNotWatchLessonsFlow(teacherId: String, departmentId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lesson WHERE teacherId = :teacherId AND departmentId = :departmentId " +
            "AND lessonStatusId != 1 AND isStatusWatched = 0 ORDER BY modifiedOn")
    fun getNotWatchLessons(teacherId: String, departmentId: String): List<LessonEntity>

    @Query("SELECT * FROM teachersDepartmentCrossRef" +
            " WHERE teacherId = :teacherId AND departmentId = :departmentId")
    fun getTeachersDepartmentCrossRef(teacherId: String, departmentId: String): TeachersDepartmentCrossRef
}