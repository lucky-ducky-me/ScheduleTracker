package com.example.scheduletrackervyatsu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.scheduletrackervyatsu.data.entities.ChangeStatusEntity
import com.example.scheduletrackervyatsu.data.entities.DepartmentEntity
import com.example.scheduletrackervyatsu.data.entities.DepartmentWithTeachers
import com.example.scheduletrackervyatsu.data.entities.LessonEntity
import com.example.scheduletrackervyatsu.data.entities.ScheduleChangeEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherEntity
import com.example.scheduletrackervyatsu.data.entities.TeacherWithDepartments
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleTrackerDao {
    @Insert
    fun insert(entity: TeacherEntity)

    @Insert
    fun insert(entity: ChangeStatusEntity)

    @Insert
    fun insert(entity: DepartmentEntity)

    @Insert
    fun insert(entity: LessonEntity)

    @Insert
    fun insert(entity: ScheduleChangeEntity)

    @Query("SELECT * FROM teacher")
    fun getAllTeachers(): List<TeacherEntity>

    @Query("SELECT * FROM changeStatus")
    fun getAllChangeStatus(): List<ChangeStatusEntity>

    @Query("SELECT * FROM department")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    @Query("SELECT * FROM lesson")
    fun getAllLessons(): List<LessonEntity>

    @Query("SELECT * FROM scheduleChange")
    fun getAllScheduleChanges(): List<ScheduleChangeEntity>

    @Transaction
    @Query("SELECT * FROM teacher")
    fun getTeacherWithDepartments(): List<TeacherWithDepartments>

    @Transaction
    @Query("SELECT * FROM department")
    fun getDepartmentWithTeachers(): List<DepartmentWithTeachers>

}