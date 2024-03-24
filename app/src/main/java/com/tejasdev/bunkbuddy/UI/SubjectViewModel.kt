package com.tejasdev.bunkbuddy.UI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    @ApplicationContext private val app: Application,
    private val repository: SubjectRepository
): AndroidViewModel(app) {

    val savedSubjects = repository.savedSubjects
    val monday = repository.monday
    val tuesday = repository.tuesday
    val wednesday = repository.wednesday
    val thursday = repository.thursday
    val friday = repository.friday
    val saturday = repository.saturday
    val sunday = repository.sunday

    val allHistory = repository.getHistory()

    fun addHistory(history: HistoryItem){
        viewModelScope.launch {
            repository.addHistoryItem(history)
        }
    }

    fun addSubject(subject: Subject){
        viewModelScope.launch {
            repository.addSubject(subject)
        }
    }

    fun updateSubjectAndLectures(subject: Subject){
        viewModelScope.launch {
            repository.updateSubjectAndLectures(subject)
        }
    }

    fun getAllSubjectSync():List<Subject>{
        return repository.getSubjectSync()
    }

    fun deleteLecture(lecture: Lecture) = repository.deleteLecture(lecture)

    fun deleteSubject(subject: Subject){
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    fun getAllLecturesSync(): List<Lecture>{
        return repository.getAllLectures()
    }

    fun updateSubject(subject: Subject){
        viewModelScope.launch {
            repository.updateSubject(subject)
        }
    }
    fun addLecture(lecture: Lecture): Int{
        return repository.addLecture(lecture)
    }

}