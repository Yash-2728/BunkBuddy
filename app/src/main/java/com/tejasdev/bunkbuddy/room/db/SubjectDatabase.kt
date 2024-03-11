package com.tejasdev.bunkbuddy.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.room.converters.TypeConverter
import com.tejasdev.bunkbuddy.room.dao.HistoryDao
import com.tejasdev.bunkbuddy.room.dao.SubjectDao
import com.tejasdev.bunkbuddy.room.migrations.MigrationFrom1to2

@Database(
    entities = [Subject::class, Lecture::class, HistoryItem::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class SubjectDatabase: RoomDatabase() {

    abstract fun getDao(): SubjectDao
    abstract fun getHistoryDao(): HistoryDao
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
                .addMigrations(MigrationFrom1to2())
                .build()

            INSTANCE = instance
            return instance
        }
    }
}

