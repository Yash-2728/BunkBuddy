package com.tejasdev.bunkbuddy.UI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.interfaces.SubjectRepositoryInterface
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val repository: SubjectRepositoryInterface
): ViewModel() {

    val savedSubjects = repository.getAllSubjects()

    val monday = repository.getLecturesForDay(0)
    val tuesday = repository.getLecturesForDay(1)
    val wednesday = repository.getLecturesForDay(2)
    val thursday = repository.getLecturesForDay(3)
    val friday = repository.getLecturesForDay(4)
    val saturday = repository.getLecturesForDay(5)
    val sunday = repository.getLecturesForDay(6)

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