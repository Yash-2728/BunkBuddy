package com.tejasdev.bunkbuddy.repository

import androidx.lifecycle.LiveData
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.interfaces.SubjectRepositoryInterface
import com.tejasdev.bunkbuddy.room.db.SubjectDatabase
import javax.inject.Inject

class SubjectRepository @Inject constructor(
    private val db: SubjectDatabase
): SubjectRepositoryInterface {

    private val dao = db.getDao()
    private val historyDao = db.getHistoryDao()

    val savedSubjects = dao.getAllSubjects()
    val monday = getLecturesForDay(0)
    val tuesday = getLecturesForDay(1)
    val wednesday = getLecturesForDay(2)
    val thursday = getLecturesForDay(3)
    val friday = getLecturesForDay(4)
    val saturday = getLecturesForDay(5)
    val sunday = getLecturesForDay(6)

    override suspend fun updateSubjectAndLectures(subject: Subject){
        dao.updateSubjectAndRelatedLectures(subject)
    }
    override fun getSubjectSync(): List<Subject>{
        return dao.getAllSubjectsSync()
    }
    override suspend fun addSubject(subject: Subject){
        dao.addSubject(subject)
    }

    override suspend fun deleteSubject(subject: Subject){
        dao.deleteSubject(subject)
    }

    override fun getAllLectures(): List<Lecture>{
        return dao.getAllLectures()
    }
    override fun updateSubject(subject: Subject){
        dao.updateSubject(subject)
    }

    override fun addLecture(lecture: Lecture):Int {
        return dao.addLecture(lecture).toInt()
    }
    override fun deleteLecture(lecture: Lecture) {
        dao.deleteLecture(lecture)
    }
    override fun getLecturesForDay(day: Int): LiveData<List<Lecture>> {
        return dao.getLecturesForDay(day)
    }

    override fun getHistory(): LiveData<List<HistoryItem>> = historyDao.getHistory()
    override suspend fun addHistoryItem(history: HistoryItem) = historyDao.addHistory(history = history)
}