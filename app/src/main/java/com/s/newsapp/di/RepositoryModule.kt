
package com.s.newsapp.di

import android.content.Context
import com.s.newsapp.data.local.NewsDao
import com.s.newsapp.api.ApiHelper
import com.s.newsapp.data.local.NewsDatabase
import com.s.newsapp.network.repository.INewsRepository
import com.s.newsapp.remote.CategoryRepository
import com.s.newsapp.remote.ICategoryRepository
import com.s.newsapp.remote.IsourceRepository
import com.s.newsapp.remote.NewsRepository
import com.s.newsapp.remote.SourceRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        NewsDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideNewsDao(db: NewsDatabase) = db.getNewsDao()

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: ApiHelper,
        localDataSource: NewsDao
    ) = NewsRepository(remoteDataSource, localDataSource)

    @Singleton
    @Provides
    fun provideINewsRepository(repository: NewsRepository): INewsRepository = repository

    @Singleton
    @Provides
    fun provideICategoryRepository(category: CategoryRepository): ICategoryRepository = category

    @Singleton
    @Provides
    fun provideISourceRepository(sources: SourceRepository): IsourceRepository = sources
}