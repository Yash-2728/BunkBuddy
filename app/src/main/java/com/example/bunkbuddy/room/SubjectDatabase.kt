package com.example.bunkbuddy.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bunkbuddy.datamodel.Lecture
import com.example.bunkbuddy.datamodel.Subject

@Database(entities = [Subject::class, Lecture::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class SubjectDatabase: RoomDatabase() {

    abstract fun getDao(): SubjectDao

    companion object{

        @Volatile
        private var INSTANCE: SubjectDatabase? = null

        fun getDatabase(context: Context): SubjectDatabase {
            if(INSTANCE !=null) return INSTANCE!!

            val instance = Room.databaseBuilder(
                context.applicationContext,
                SubjectDatabase::class.java,
                "subject_database")
                .allowMainThreadQueries()
                .build()

            INSTANCE = instance
            return instance
        }
    }
}