package com.s.newsapp.api

import com.s.newsapp.data.model.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.s.newsapp.data.model.NewsResponse
import com.s.newsapp.data.model.SourceResponse
import com.s.newsapp.utils.Constants.Companion.API_KEY
import com.s.newsapp.utils.Constants.Companion.COUNTRY_CODE
import com.s.newsapp.utils.Constants.Companion.QUERY_PER_PAGE

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("sources")
        sources: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>


    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>


    @GET("v2/top-headlines/sources")
    suspend fun getCategory(
        @Query("country")
        countryCode: String = COUNTRY_CODE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<CategoryResponse>

    @GET("v2/top-headlines/sources")
    suspend fun getSource(
        @Query("category")
        category: String ,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<SourceResponse>

}