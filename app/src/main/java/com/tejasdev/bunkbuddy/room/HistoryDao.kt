package com.tejasdev.bunkbuddy.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tejasdev.bunkbuddy.datamodel.HistoryItem

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHistory(history: HistoryItem)

    @Query("SELECT * FROM history")
    fun getHistory(): LiveData<List<HistoryItem>>

}