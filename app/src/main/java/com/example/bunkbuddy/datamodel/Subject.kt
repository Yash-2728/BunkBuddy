package com.example.bunkbuddy.datamodel

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName="subjects")
@Parcelize
data class Subject(
    var name: String,
    var missed: Int,
    var attended: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var lastUpdated: String,
    var requirement: Int,
) : Parcelable
