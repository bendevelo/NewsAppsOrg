package com.s.newsapp.api

import com.s.newsapp.data.model.CategoryResponse
import com.s.newsapp.data.model.NewsResponse
import com.s.newsapp.data.model.SourceResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImp @Inject constructor(private val newsApi: NewsApi) : ApiHelper {

    override suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse> =
        newsApi.getNews(countryCode, pageNumber)

    override suspend fun getCategory(countryCode: String): Response<CategoryResponse> =
        newsApi.getCategory(countryCode)

    override suspend fun getSource(category: String): Response<SourceResponse> =
        newsApi.getSource(category)


    override suspend fun searchNews(query: String, pageNumber: Int): Response<NewsResponse> =
        newsApi.searchNews(query, pageNumber)


}