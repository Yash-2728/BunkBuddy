package com.example.bunkbuddy.repository

import com.example.bunkbuddy.datamodel.Subject
import com.example.bunkbuddy.room.SubjectDatabase

class SubjectRepository(val db: SubjectDatabase) {

    private val dao = db.getDao()

    val savedSubjects = dao.getAllSubjects()

    suspend fun addSubject(subject: Subject){
        dao.addSubject(subject)
    }

    suspend fun deleteSubject(subject: Subject){
        dao.deleteSubject(subject)
    }

    suspend fun updateSubject(subject: Subject){
        dao.updateSubject(subject)
    }
}