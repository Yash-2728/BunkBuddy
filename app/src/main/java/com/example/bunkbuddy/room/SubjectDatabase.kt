package com.example.bunkbuddy.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bunkbuddy.datamodel.Subject

@Database(entities = [Subject::class], version = 1, exportSchema = false)
abstract class SubjectDatabase: RoomDatabase() {

    abstract fun getDao(): SubjectDao

    companion object{

        @Volatile
        private var INSTANCE: SubjectDatabase? = null

        fun getDatabase(context: Context): SubjectDatabase{
            if(INSTANCE!=null) return INSTANCE!!

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