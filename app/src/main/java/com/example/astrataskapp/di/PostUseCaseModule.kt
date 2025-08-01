package com.example.astrataskapp.di

import com.example.astrataskapp.domain.repository.PostRepository
import com.example.astrataskapp.domain.use_case.PostUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostUseCaseModule {

    @Provides
    @Singleton
    fun providePostUseCase(repository: PostRepository): PostUseCase {
        return PostUseCase(repository)
    }
}