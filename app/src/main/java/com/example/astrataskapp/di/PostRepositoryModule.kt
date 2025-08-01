package com.example.astrataskapp.di

import android.app.Application
import com.example.astrataskapp.data.remote.PostApi
import com.example.astrataskapp.data.repository.PostRepositoryImpl
import com.example.astrataskapp.domain.repository.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostRepositoryModule {

    @Provides
    @Singleton
    fun providePostRepository(postApi: PostApi, application: Application): PostRepository {
        return PostRepositoryImpl(postApi = postApi, application = application)
    }
}