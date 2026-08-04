package com.example.mediaconverter.di

import android.content.Context
import androidx.room.Room
import com.example.mediaconverter.data.HistoryDao
import com.example.mediaconverter.data.MediaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): MediaDatabase {
        return Room.databaseBuilder(
            appContext,
            MediaDatabase::class.java,
            "media_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideHistoryDao(database: MediaDatabase): HistoryDao {
        return database.historyDao()
    }
}