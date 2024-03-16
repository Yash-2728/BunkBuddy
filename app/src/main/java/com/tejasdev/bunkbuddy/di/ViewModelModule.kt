package com.tejasdev.bunkbuddy.di

import android.app.Application
import android.content.Context
import com.tejasdev.bunkbuddy.UI.AuthViewmodel
import com.tejasdev.bunkbuddy.repository.AuthRepository
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
    ): AuthViewmodel{
        return AuthViewmodel(app, repo)
    }
}