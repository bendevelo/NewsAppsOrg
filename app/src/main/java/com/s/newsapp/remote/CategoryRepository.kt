package com.s.newsapp.remote

import com.s.newsapp.api.ApiHelper
import com.s.newsapp.data.model.CategoryResponse
import com.s.newsapp.data.model.SourceResponse
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val remoteDataSource: ApiHelper
): ICategoryRepository{


    override suspend fun getCategory(countryCode: String): NetworkState<CategoryResponse> {
        return try {
            val response = remoteDataSource.getCategory(countryCode)
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
}