package com.s.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s.newsapp.data.model.CategoryResponse
import com.s.newsapp.data.model.NewsResponse
import com.s.newsapp.data.model.SourceResponse
import com.s.newsapp.di.CoroutinesDispatcherProvider
import com.s.newsapp.network.repository.INewsRepository
import com.s.newsapp.remote.ICategoryRepository
import com.s.newsapp.remote.IsourceRepository
import com.s.newsapp.remote.NetworkState
import com.s.newsapp.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: ICategoryRepository,
    private val repositorySource: IsourceRepository,
    private val networkHelper: NetworkHelper,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    var searchEnable: Boolean = false
    var searchNewsPage = 1
    private var searchResponse: NewsResponse? = null
    private var oldQuery: String = ""
    var newQuery: String = ""

    private val _categoryResponse = MutableStateFlow<NetworkState<CategoryResponse>>(NetworkState.Empty())
    val categoryResponse: StateFlow<NetworkState<CategoryResponse>>
        get() = _categoryResponse

    private val _sourceResponse = MutableStateFlow<NetworkState<SourceResponse>>(NetworkState.Empty())
    val sourceResponse: StateFlow<NetworkState<SourceResponse>>
        get() = _sourceResponse

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String>
        get() = _errorMessage

    fun fetchCategory(countryCode: String) {

        if (networkHelper.isNetworkConnected()) {
            viewModelScope.launch(coroutinesDispatcherProvider.io) {
                _categoryResponse.value = NetworkState.Loading()
                when (val response = repository.getCategory(countryCode)) {
                    is NetworkState.Success -> {
                        _categoryResponse.value = response
                    }
                    is NetworkState.Error -> {
                        _categoryResponse.value =
                            NetworkState.Error(
                                response.message ?: "Error"
                            )
                    }
                    else -> {}
                }

            }
        } else {
            _errorMessage.value = "No internet available"
        }

    }

    private var feedResponse: SourceResponse? = null
    var feedNewsPage = 1
    var totalPage = 1

    fun fetchSource(category: String) {

        if (feedNewsPage <= totalPage) {
            if (networkHelper.isNetworkConnected()) {
                viewModelScope.launch(coroutinesDispatcherProvider.io) {
                    _sourceResponse.value = NetworkState.Loading()
                    when (val response = repositorySource.getSource(category)) {
                        is NetworkState.Success -> {
                            _sourceResponse.value =handlesourceResponse( response)
                        }

                        is NetworkState.Error -> {
                            _sourceResponse.value =
                                NetworkState.Error(
                                    response.message ?: "Error"
                                )
                        }

                        else -> {

                        }
                    }

                }
            } else {
                _errorMessage.value = "No internet available"
            }
        }
    }

    private fun handlesourceResponse(response: NetworkState<SourceResponse>): NetworkState<SourceResponse> {
        response.data?.let { resultResponse ->
            if (feedResponse == null) {
                feedNewsPage = 2
                feedResponse = resultResponse
            } else {
                feedNewsPage++
                val oldArticles = feedResponse?.sources
                val newArticles = resultResponse.sources
                oldArticles?.addAll(newArticles)
            }
            //Conversion
//            feedResponse?.let {
//                convertPublishedDate(it)
//            }
            return NetworkState.Success(feedResponse ?: resultResponse)
        }
        return NetworkState.Error("No data found")
    }

    fun clearSearch() {
        searchEnable = false
        searchResponse = null
        feedResponse = null
        feedNewsPage = 1
        searchNewsPage = 1
    }

    fun hideErrorToast() {
        _errorMessage.value = ""
    }

}