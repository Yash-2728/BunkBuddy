package com.tejasdev.bunkbuddy.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract

import androidx.lifecycle.AndroidViewModel
import com.tejasdev.bunkbuddy.datamodel.User
import com.tejasdev.bunkbuddy.repository.AuthRepository
import com.tejasdev.bunkbuddy.session.Session

class AuthViewmodel(
    private val app: Application,
    private val context: Context
): AndroidViewModel(app) {

    private val repo = AuthRepository(context)
    private val session = Session.getInstance(context)

    fun updateImage(image: String){
        session.updateUserImage(image)
    }
    fun updateUserName(username: String) {
        session.updateUserName(username)
    }

    fun isLogin():Boolean = session.isLogin()

    fun createSession(user: User){
        user.apply {
            session.createSession(name, email, id)
        }
    }

    fun getUserId(): String = session.getUserId()
    fun getUserImage(): Uri = session.getUserImage()
    fun getUserName(): String = session.getUserName()
    fun getEmail(): String = session.getEmail()
    fun loginUser(email: String, password: String, callback:(User?, String?)->Unit){
        repo.login(email, password){ user, message ->
            callback(user, message)
        }
    }

   fun signOut(){
       session.signOut()
   }

    fun signupUser(email: String, name: String, password: String, callback: (User?, String?)->Unit){
        repo.signup(name, email, password){user, message ->
            callback(user, message)
        }
    }
    fun changePassword(email: String, currPassword: String, newPassword: String, callback: (User?, String?)->Unit){
        repo.changePassword(email, currPassword, newPassword){user, message ->
            callback(user, message)
        }
    }
    fun updateProfilePic(email: String, newImage: String, callback: (User?, String?)-> Unit){
        repo.changeProfilePicture(email, newImage){user, message ->
            callback(user, message)
        }
    }

    fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork?:return false
            val capability = connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
            return when{
                capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run{
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ContactsContract.CommonDataKinds.Email.TYPE_MOBILE ->  true
                    ConnectivityManager.TYPE_ETHERNET ->  true
                    else -> false
                }
            }
        }
        return false
    }
}