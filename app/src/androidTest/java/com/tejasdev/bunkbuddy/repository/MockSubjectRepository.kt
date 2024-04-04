package com.tejasdev.bunkbuddy.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.interfaces.SubjectRepositoryInterface


class MockSubjectRepository: SubjectRepositoryInterface{

    private val subjectItems = mutableListOf<Subject>()
    private val lectureItems = mutableListOf<Lecture>()
    val observableSubjectItems = MutableLiveData<List<Subject>>(subjectItems)
    val observableLectureItems = MutableLiveData<List<Lecture>>(lectureItems)
    private var shouldReturnNetworkError = false

    private fun refreshLiveData(){
        observableSubjectItems.postValue(subjectItems)
        observableLectureItems.postValue(lectureItems)
    }

    override suspend fun updateSubjectAndLectures(subject: Subject) {

    }

    override fun getSubjectSync(): List<Subject> {
        TODO("Not yet implemented")
    }

    override suspend fun addSubject(subject: Subject) {
        subjectItems.add(subject)
        refreshLiveData()
    }

    override suspend fun deleteSubject(subject: Subject) {
        subjectItems.remove(subject)
        refreshLiveData()
    }

    override fun getAllLectures(): List<Lecture> {
        return lectureItems
    }

    override fun updateSubject(subject: Subject) {}

    override fun addLecture(lecture: Lecture): Int {
        lectureItems.add(lecture)
        return lecture.pid
    }

    override fun deleteLecture(lecture: Lecture) {
        lectureItems.remove(lecture)
    }

    override fun getLecturesForDay(day: Int): LiveData<List<Lecture>> {
        val list = mutableListOf<Lecture>()
        for(lecture in lectureItems){
            if(lecture.dayNumber == day) list.add(lecture)
        }
        return liveData {}
    }

    override fun getHistory(): LiveData<List<HistoryItem>> {
        return liveData {  }
    }

    override suspend fun addHistoryItem(history: HistoryItem) {}

}