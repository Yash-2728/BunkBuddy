package com.example.bunkbuddy.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bunkbuddy.datamodel.Lecture
import com.example.bunkbuddy.datamodel.Subject

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubject(subject: Subject)

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): LiveData<List<Subject>>

    @Query("SELECT * FROM subjects")
    fun getAllSubjectsSync(): List<Subject>
    @Update
    fun updateSubject(subject: Subject)
    @Delete
    suspend fun deleteSubject(subject: Subject)



    @Query("SELECT * FROM lectures WHERE dayNumber= :day")
    fun getLecturesForDay(day: Int): LiveData<List<Lecture>>
    @Query("SELECT * FROM lectures")
    fun getAllLectures(): LiveData<List<Lecture>>
    @Insert
    fun addLecture(lecture: Lecture)
    @Update
    fun updateLecture(lecture: Lecture)
    @Delete
    fun deleteLecture(lecture: Lecture)

}