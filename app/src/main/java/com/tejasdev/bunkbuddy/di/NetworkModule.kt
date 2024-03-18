package com.tejasdev.bunkbuddy.di

import com.tejasdev.bunkbuddy.api.AuthAPI
import com.tejasdev.bunkbuddy.repository.AuthRepository
import com.tejasdev.bunkbuddy.secret.Secret
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule{

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor{
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }

    @Singleton
    @Provides
    fun provideClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideConvertorFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(
        baseUrl: String,
        converter: Converter.Factory,
        okHttpClient: OkHttpClient
    ): Retrofit{
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(converter)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthAPI(
        retrofit: Retrofit
    ): AuthAPI{
        return retrofit.create(AuthAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        apiService: AuthAPI
    ): AuthRepository{
        return AuthRepository(
            apiService
        )
    }

    @Provides
    fun provideBaseUrl(): String{
        return Secret.BASE_URL
    }
}