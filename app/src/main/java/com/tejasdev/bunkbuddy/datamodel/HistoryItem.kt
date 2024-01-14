package com.tejasdev.bunkbuddy.datamodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "history"
)
data class HistoryItem (
    val actionType: Int,
    val message: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: String,
    val date: String
)