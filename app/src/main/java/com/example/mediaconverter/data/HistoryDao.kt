package com.example.mediaconverter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(item: HistoryItem): Long

    @Query("SELECT * FROM history ORDER BY created_at DESC")
    fun getAll(): List<HistoryItem>

    @Query("SELECT * FROM history ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<HistoryItem>>

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}