package com.tejasdev.bunkbuddy.datamodel

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("__v") var v: Int = 0,
    @SerializedName("_id") var id: String = "",
    var createdAt: String = "",
    val email: String,
    val name: String,
    val password: String,
    var updatedAt: String = "",
    val image: String
)