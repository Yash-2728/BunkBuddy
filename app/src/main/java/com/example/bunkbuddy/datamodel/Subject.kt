package com.example.bunkbuddy.datamodel

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName="Subjects")
@Parcelize
data class Subject(
    val name: String,
    val missed: Int,
    val attended: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val lastUpdated: String
) : Parcelable