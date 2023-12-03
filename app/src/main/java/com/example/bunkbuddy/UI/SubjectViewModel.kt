package com.example.bunkbuddy.UI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bunkbuddy.datamodel.Lecture
import com.example.bunkbuddy.datamodel.Subject
import com.example.bunkbuddy.repository.SubjectRepository
import kotlinx.coroutines.launch

class SubjectViewModel(val app: Application, private val repository: SubjectRepository): AndroidViewModel(app) {

    val savedSubjects = repository.savedSubjects
    val monday = repository.monday
    val tuesday = repository.tuesday
    val wednesday = repository.wednesday
    val thursday = repository.thursday
    val friday = repository.friday
    val saturday = repository.saturday
    val sunday = repository.sunday


    fun addSubject(subject: Subject){
        viewModelScope.launch {
            repository.addSubject(subject)
        }
    }

    fun getAllSubjectSync():List<Subject>{
        return repository.getSubjectSync()
    }

    fun deleteSubject(subject: Subject){
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    fun updateSubject(subject: Subject){
        viewModelScope.launch {
            repository.updateSubject(subject)
        }
    }
    fun addLecture(lecture: Lecture){
        repository.addLecture(lecture)
    }

}