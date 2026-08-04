package com.example.mediaconverter.domain.repository

import com.example.mediaconverter.data.HistoryItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    suspend fun saveHistory(item: HistoryItem): Long
    fun getHistoryFlow(): Flow<List<HistoryItem>>
    suspend fun deleteHistoryItem(id: Long)
    suspend fun clearHistory()
}