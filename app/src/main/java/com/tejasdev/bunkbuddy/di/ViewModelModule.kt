package com.tejasdev.bunkbuddy.di

import android.app.Application
import com.tejasdev.bunkbuddy.UI.AlarmViewModel
import com.tejasdev.bunkbuddy.UI.AuthViewModel
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.repository.AuthRepository
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Provides
    fun provideAuthViewModel(
        app: Application,
        repo: AuthRepository
    ): AuthViewModel{
        return AuthViewModel(app, repo)
    }

    @Provides
    fun provideSubjectViewModel(
        repo: SubjectRepository
    ): SubjectViewModel{
        return SubjectViewModel(repo)
    }

    @Provides
    fun provideAlarmViewModel(
        app: Application
    ): AlarmViewModel{
        return AlarmViewModel(app)
    }
}

