
package com.s.newsapp.remote

import com.s.newsapp.data.local.NewsDao
import com.s.newsapp.api.ApiHelper
import com.s.newsapp.data.model.NewsArticle
import com.s.newsapp.data.model.NewsResponse
import com.s.newsapp.network.repository.INewsRepository
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val remoteDataSource: ApiHelper,
    private val localDataSource: NewsDao
) : INewsRepository {

    override suspend fun getNews(
        countryCode: String,
        pageNumber: Int
    ): NetworkState<NewsResponse> {
        return try {
            val response = remoteDataSource.getNews(countryCode, pageNumber)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                NetworkState.Success(result)
            } else {
                NetworkState.Error("An error occurred")
            }
        } catch (e: Exception) {
            NetworkState.Error("Error occurred ${e.localizedMessage}")
        }
    }

    override suspend fun searchNews(
        searchQuery: String,
        pageNumber: Int
    ): NetworkState<NewsResponse> {
        return try {
            val response = remoteDataSource.searchNews(searchQuery, pageNumber)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                NetworkState.Success(result)
            } else {
                NetworkState.Error("An error occurred")
            }
        } catch (e: Exception) {
            NetworkState.Error("Error occurred ${e.localizedMessage}")
        }
    }

    override suspend fun saveNews(news: NewsArticle) = localDataSource.upsert(news)

    override fun getSavedNews() = localDataSource.getAllNews()

    override suspend fun deleteNews(news: NewsArticle) = localDataSource.deleteNews(news)

    override suspend fun deleteAllNews() = localDataSource.deleteAllNews()
}