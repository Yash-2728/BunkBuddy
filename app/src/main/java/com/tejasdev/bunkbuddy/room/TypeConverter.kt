package com.tejasdev.bunkbuddy.room

import androidx.room.TypeConverter
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.google.gson.Gson

class TypeConverter {
    @TypeConverter
    fun fromSubject(subject: Subject): String?{
        return subject.let{ Gson().toJson(it) }
    }

    @TypeConverter
    fun toSubject(subjectJson: String): Subject {
        return subjectJson.let{ Gson().fromJson(it, Subject::class.java) }
    }
}