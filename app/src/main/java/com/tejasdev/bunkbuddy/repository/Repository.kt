package com.tejasdev.bunkbuddy.repository

import androidx.lifecycle.LiveData
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.room.SubjectDatabase

class SubjectRepository(private val db: SubjectDatabase) {

    private val dao = db.getDao()

    val savedSubjects = dao.getAllSubjects()
    val monday = dao.getLecturesForDay(0)
    val tuesday = dao.getLecturesForDay(1)
    val wednesday = dao.getLecturesForDay(2)
    val thursday = dao.getLecturesForDay(3)
    val friday = dao.getLecturesForDay(4)
    val saturday = dao.getLecturesForDay(5)
    val sunday = dao.getLecturesForDay(6)

    suspend fun updateSubjectAndLectures(subject: Subject){
        dao.updateSubjectAndRelatedLectures(subject)
    }
    fun getSubjectSync(): List<Subject>{
        return dao.getAllSubjectsSync()
    }
    suspend fun addSubject(subject: Subject){
        dao.addSubject(subject)
    }

    suspend fun deleteSubject(subject: Subject){
        dao.deleteSubject(subject)
    }

    fun getAllLectures(): List<Lecture>{
        return dao.getAllLectures()
    }
    fun updateSubject(subject: Subject){
        dao.updateSubject(subject)
    }

    fun addLecture(lecture: Lecture):Int {
        return dao.addLecture(lecture).toInt()
    }
    fun deleteLecture(lecture: Lecture) {
        dao.deleteLecture(lecture)
    }
    fun getLecturesForDay(day: Int): LiveData<List<Lecture>> {
        return dao.getLecturesForDay(day)
    }
}