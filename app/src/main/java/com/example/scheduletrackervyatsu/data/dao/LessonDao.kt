package com.example.scheduletrackervyatsu.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.scheduletrackervyatsu.data.entities.LessonEntity

@Dao
interface LessonDao {

    @Query("SELECT * FROM lesson")
    fun getAllLessons(): List<LessonEntity>
}