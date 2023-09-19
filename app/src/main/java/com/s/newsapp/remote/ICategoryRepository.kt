package com.s.newsapp.remote

import com.s.newsapp.data.model.CategoryResponse


interface ICategoryRepository {

    suspend fun getCategory(countryCode: String): NetworkState<CategoryResponse>
}