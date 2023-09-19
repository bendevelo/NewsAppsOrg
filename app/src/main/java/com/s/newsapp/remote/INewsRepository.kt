

package com.s.newsapp.network.repository

import androidx.lifecycle.LiveData
import com.s.newsapp.data.model.NewsArticle
import com.s.newsapp.data.model.NewsResponse
import com.s.newsapp.remote.NetworkState


interface INewsRepository {
    suspend fun getNews(countryCode: String, pageNumber: Int): NetworkState<NewsResponse>

    suspend fun searchNews(searchQuery: String, pageNumber: Int): NetworkState<NewsResponse>

    suspend fun saveNews(news: NewsArticle): Long

    fun getSavedNews(): LiveData<List<NewsArticle>>

    suspend fun deleteNews(news: NewsArticle)

    suspend fun deleteAllNews()
}
