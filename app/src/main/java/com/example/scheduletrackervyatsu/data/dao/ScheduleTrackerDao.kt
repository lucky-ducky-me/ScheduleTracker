package com.example.scheduletrackervyatsu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.scheduletrackervyatsu.data.entities.LessonEntity

@Dao
interface ScheduleTrackerDao {
    @Insert
    fun insertLesson(lesson: LessonEntity)

    @Query("SELECT * FROM lesson")
    fun getAllLessons(): List<LessonEntity>
}