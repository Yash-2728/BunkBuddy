package com.example.bunkbuddy.UI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunkbuddy.datamodel.Subject
import com.example.bunkbuddy.repository.SubjectRepository
import kotlinx.coroutines.launch

class SubjectViewModel(val app: Application, private val repository: SubjectRepository): AndroidViewModel(app) {

    val savedSubjects = repository.savedSubjects

    fun addSubject(subject: Subject){
        viewModelScope.launch {
            repository.addSubject(subject)
        }
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

}