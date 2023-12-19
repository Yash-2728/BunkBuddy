package com.tejasdev.bunkbuddy.api

import com.tejasdev.bunkbuddy.datamodel.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthAPI {
    @GET("/login")
    fun loginUser(
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<User>

    @GET("/signup")
    fun signupUser(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("image") image: String
    ): Call<User>

    @POST("/update-profile-picture")
    fun updateProfilePic(
        @Query("email") email: String,
        @Query("imageUrl") imageUrl: String
    ): Call<User>

    @GET("/update-password")
    fun updatePassword(
        @Query("email") email: String,
        @Query("currentPassword") currPassword: String,
        @Query("newPassword") newPassword: String
    ): Call<User>

    @POST("/update-username")
    fun updateUsername(
        @Query("email") email: String,
        @Query("newUsername") newUsername: String,
        @Query("password") password: String
    ): Call<User>
}