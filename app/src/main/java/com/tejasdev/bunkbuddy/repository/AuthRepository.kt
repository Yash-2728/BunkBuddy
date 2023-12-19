package com.tejasdev.bunkbuddy.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.tejasdev.bunkbuddy.api.RetrofitInstance
import com.tejasdev.bunkbuddy.datamodel.ErrorResponse
import com.tejasdev.bunkbuddy.datamodel.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val context: Context){
    private val retrofit = RetrofitInstance

    fun login(email: String, password: String, callback: (User?, String?)-> Unit){
        val call = retrofit.api.loginUser(email, password)

        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    Log.w("api-check ", "error ${response.errorBody()?:"null"}")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

    fun changePassword(email: String, currentPassword: String, newPassword: String, callback: (User?, String?)->Unit){
        val call = retrofit.api.updatePassword(email, currentPassword, newPassword)
        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

    fun changeUsername(email: String, newUsername: String, password: String, callback: (User?, String?)-> Unit){
        val call = retrofit.api.updateUsername(email, newUsername, password)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }
    fun changeProfilePicture(email: String, newImageUrl: String, callback: (User?, String?)->Unit){
        val call = retrofit.api.updateProfilePic(email, newImageUrl)

        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

    fun signup(name: String, email: String, password: String, image: String, callback: (User?, String?)-> Unit){
        val call = retrofit.api.signupUser(name, email, password, image)

        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }
}