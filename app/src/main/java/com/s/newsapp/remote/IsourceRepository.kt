package com.s.newsapp.remote

import com.s.newsapp.data.model.CategoryResponse
import com.s.newsapp.data.model.SourceResponse

interface IsourceRepository {

    suspend fun getSource(category: String): NetworkState<SourceResponse>
}