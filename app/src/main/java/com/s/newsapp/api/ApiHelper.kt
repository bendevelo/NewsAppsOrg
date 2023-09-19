package com.s.newsapp.api

import com.s.newsapp.data.model.CategoryResponse
import com.s.newsapp.data.model.NewsResponse
import com.s.newsapp.data.model.SourceResponse
import retrofit2.Response

interface ApiHelper {

    suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse>
    suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse>
    suspend fun getCategory(countryCode: String): Response<CategoryResponse>
    suspend fun getSource(category: String): Response<SourceResponse>
}