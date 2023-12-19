package com.tejasdev.bunkbuddy.session

import android.content.Context
import android.net.Uri
import android.util.Log
import com.tejasdev.bunkbuddy.activities.AuthActivity

class Session private constructor(context: Context){

    private val sharedPref = context.getSharedPreferences(
        Session.SHARED_PREF,
        Context.MODE_PRIVATE
    )
    private val editor = sharedPref.edit()

    fun isLogin(): Boolean{
        return sharedPref.getBoolean(IS_LOGIN, false)
    }

    fun loginSkipped(){
        editor.putBoolean(LOGIN_SKIPPED, true);
        editor.apply()
    }
    fun loginNotSkipped(){
        editor.putBoolean(LOGIN_SKIPPED, false)
        editor.apply()
    }
    fun isSkipped(): Boolean{
        return sharedPref.getBoolean(LOGIN_SKIPPED, false)
    }

    fun getUserId(): String{
        return sharedPref.getString(USER_ID, "")?: ""
    }

    fun createSession(username: String, email: String, userId: String, image: String){
        editor.putString(USERNAME, username)
        editor.putString(EMAIL, email)
        editor.putString(IMAGE, image)
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(USER_ID, userId)
        editor.apply()
    }

    fun updateUserImage(newImage: String){
        editor.putString(IMAGE, newImage)
        editor.apply()
    }

    fun updateUserName(newUsername: String){
        editor.putString(USERNAME, newUsername)
        editor.apply()
    }


    fun getUserImage(): Uri {
        Log.w("image-upload", sharedPref.getString(IMAGE, "")!!)
        return Uri.parse(sharedPref.getString(IMAGE, ""))
    }

    fun signOut() {
        editor.clear()
        editor.apply()
    }

    fun getUserName(): String = sharedPref.getString(USERNAME, "")!!
    fun getEmail(): String = sharedPref.getString(EMAIL, "")!!


    companion object{
        private var instance: Session? = null

        fun getInstance(context: Context): Session{
            return instance?: synchronized(this){
                instance?: Session(context).also { instance = it }
            }
        }
        const val SHARED_PREF = "bunkbuddy_login_sharedPref"
        const val IS_LOGIN = "isLoggedIn"
        const val USER_ID = "userid"
        const val USERNAME = "username"
        const val EMAIL = "useremail"
        const val IMAGE = "image"
        const val LOGIN_SKIPPED = "login_skipped"
    }
}