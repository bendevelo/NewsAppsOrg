package com.s.newsapp.remote

import com.s.newsapp.api.ApiHelper
import com.s.newsapp.data.model.SourceResponse
import javax.inject.Inject

class SourceRepository @Inject constructor(
    private val remoteDataSource: ApiHelper
): IsourceRepository {
    override suspend fun getSource(category: String): NetworkState<SourceResponse> {
        return try {
            val response = remoteDataSource.getSource(category)
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