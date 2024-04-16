package com.tejasdev.bunkbuddy.di

import android.content.Context
import androidx.room.Room
import com.tejasdev.bunkbuddy.room.db.SubjectDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    fun provideDb(
        @ApplicationContext context: Context
    ): SubjectDatabase{
        return Room.databaseBuilder(
            context,
            SubjectDatabase::class.java,
            "test_db"
            )
            .allowMainThreadQueries()
            .build()
    }

}