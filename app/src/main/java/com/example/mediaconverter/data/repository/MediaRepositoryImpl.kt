package com.example.mediaconverter.data.repository

import com.example.mediaconverter.data.HistoryDao
import com.example.mediaconverter.data.HistoryItem
import com.example.mediaconverter.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : MediaRepository {

    override suspend fun saveHistory(item: HistoryItem): Long {
        return historyDao.insert(item)
    }

    override fun getHistoryFlow(): Flow<List<HistoryItem>> {
        return historyDao.getAllFlow()
    }

    override suspend fun deleteHistoryItem(id: Long) {
        historyDao.deleteById(id)
    }

    override suspend fun clearHistory() {
        historyDao.deleteAll()
    }
}