package com.example.mediaconverter.di

import com.example.mediaconverter.data.repository.MediaRepositoryImpl
import com.example.mediaconverter.domain.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMediaRepository(repositoryImpl: MediaRepositoryImpl): MediaRepository {
        return repositoryImpl
    }
}