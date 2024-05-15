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

    @Query("DELETE FROM teachersDepartmentCrossRef WHERE teacherId = :teacherId")
    fun deleteAllTrackingForTeacher(teacherId: String)


}