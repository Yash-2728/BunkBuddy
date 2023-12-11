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
//class TypeConverter {
//    @androidx.room.TypeConverter
//    fun fromOwner(owner: Owner?): String? {
//        // Convert Owner to String representation for storage in the database
//        return owner?.let { Gson().toJson(it) }
//    }
//
//    @androidx.room.TypeConverter
//    fun toOwner(ownerString: String?): Owner? {
//        // Convert String representation from the database to Owner object
//        return ownerString?.let { Gson().fromJson(it, Owner::class.java) }
//    }
//}