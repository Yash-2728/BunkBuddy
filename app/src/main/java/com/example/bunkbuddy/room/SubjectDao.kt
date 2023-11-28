package com.example.bunkbuddy.room

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bunkbuddy.datamodel.Subject

interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubject(subject: Subject)

    @Query("SELECT * FROM Subjects")
    suspend fun getAllSubjects(): LiveData<List<Subject>>

    @Update
    fun updateSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)
}