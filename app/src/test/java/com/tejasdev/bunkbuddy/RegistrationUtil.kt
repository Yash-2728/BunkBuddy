package com.tejasdev.bunkbuddy

object RegistrationUtil{
    fun checkIfCredentialsValid(
        userName: String,
        password: String,
        confirmPassword: String
    ): Boolean{
        return userName.isNotEmpty() &&
                password.equals(confirmPassword) &&
                password.length>7
    }
}