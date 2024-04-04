package com.tejasdev.bunkbuddy.di

import android.content.Context
import androidx.room.Room
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import com.tejasdev.bunkbuddy.room.db.SubjectDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SubjectDatabase{
        return Room.databaseBuilder(
            context,
            SubjectDatabase::class.java,
            "subject_database",
        )
            .allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideRepository(database: SubjectDatabase): SubjectRepository{
        return SubjectRepository(database)
    }


}