package com.s.newsapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.s.newsapp.Adapter.NewsAdapter
import com.s.newsapp.remote.NetworkState
import com.s.newsapp.base.BaseFragment
import com.s.newsapp.databinding.FragmentCategoryBinding
import com.s.newsapp.utils.Constants
import com.s.newsapp.utils.Constants.Companion.QUERY_PER_PAGE
import com.s.newsapp.utils.EndlessRecyclerOnScrollListener
import com.s.newsapp.utils.EspressoIdlingResource
import com.s.newsapp.viewmodel.MainViewModel


class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {
    override fun setBinding(): FragmentCategoryBinding =
        FragmentCategoryBinding.inflate(layoutInflater)

    val args: CategoryFragmentArgs by navArgs()

    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener
    lateinit var mainViewModel: MainViewModel
    private lateinit var newsAdapter: NewsAdapter
    val countryCode = Constants.COUNTRY_CODE
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        val source = args.source.id
        Log.d("sumber", source)
        mainViewModel.clearSearch()
        mainViewModel.fetchNews(source)

        setupUI(source)
        setupRecyclerView()
        setupObservers()
        setHasOptionsMenu(true)
    }

    private fun setupUI(source: String) {
        EspressoIdlingResource.increment()
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            if (mainViewModel.searchEnable) {
                mainViewModel.searchNews(mainViewModel.newQuery)
            } else {
                mainViewModel.fetchNews(source)
            }
            hideErrorMessage()
        }

        // scroll listener for recycler view
        onScrollListener = object : EndlessRecyclerOnScrollListener(QUERY_PER_PAGE) {
            override fun onLoadMore() {
                if (mainViewModel.searchEnable) {
                    mainViewModel.searchNews(mainViewModel.newQuery)
                } else {
                    mainViewModel.fetchNews(source)
                }
            }
        }

        //Swipe refresh listener
        val refreshListener = SwipeRefreshLayout.OnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            mainViewModel.clearSearch()
            mainViewModel.fetchNews(source)
        }
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(onScrollListener)
        }
        newsAdapter.setOnItemClickListener { news ->
            val bundle = Bundle().apply {
                putSerializable("news", news)
            }
            findNavController().navigate(
                R.id.action_categoryFragment_to_detailedFragment,
                bundle
            )
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            if (!mainViewModel.searchEnable) {
                mainViewModel.newsResponse.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {
                            hideProgressBar()
                            hideErrorMessage()
                            response.data?.let { newResponse ->
                                EspressoIdlingResource.decrement()
                                Log.d("sumberku", newResponse.articles.size.toString())
                                newsAdapter.differ.submitList(newResponse.articles.toList())
                                mainViewModel.totalPage =
                                    newResponse.totalResults / QUERY_PER_PAGE + 1
                                onScrollListener.isLastPage =
                                    mainViewModel.feedNewsPage == mainViewModel.totalPage + 1
                                hideBottomPadding()
                            }
                        }

                        is NetworkState.Loading -> {
                            showProgressBar()
                        }

                        is NetworkState.Error -> {
                            hideProgressBar()
                            response.message?.let {
                                showErrorMessage(response.message)
                            }
                        }

                        else -> {}
                    }
                }
            } else {
                collectSearchResponse()
            }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.errorMessage.collect { value ->
                if (value.isNotEmpty()) {
                    Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
                }
                mainViewModel.hideErrorToast()
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.errorCard.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = message
        onScrollListener.isError = true
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.errorCard.visibility = View.GONE
        onScrollListener.isError = false
    }

    private fun collectSearchResponse() {
        //Search response
        lifecycleScope.launchWhenStarted {
            if (mainViewModel.searchEnable) {
                mainViewModel.searchNewsResponse.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {
                            hideProgressBar()
                            hideErrorMessage()
                            response.data?.let { searchResponse ->
                                EspressoIdlingResource.decrement()
                                newsAdapter.differ.submitList(searchResponse.articles.toList())
                                mainViewModel.totalPage =
                                    searchResponse.totalResults / QUERY_PER_PAGE + 1
                                onScrollListener.isLastPage =
                                    mainViewModel.searchNewsPage == mainViewModel.totalPage + 1
                                hideBottomPadding()
                            }
                        }

                        is NetworkState.Loading -> {
                            showProgressBar()
                        }

                        is NetworkState.Error -> {
                            hideProgressBar()
                            response.message?.let {
                                showErrorMessage(response.message)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun hideBottomPadding() {
        if (onScrollListener.isLastPage) {
            binding.rvNews.setPadding(0, 0, 0, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        //Search button clicked
        searchView.setOnSearchClickListener {
            searchView.maxWidth = android.R.attr.width
        }
        //Close button clicked
        searchView.setOnCloseListener {
            mainViewModel.clearSearch()
            mainViewModel.fetchNews(countryCode)
            //Collapse the action view
            searchView.onActionViewCollapsed()
            searchView.maxWidth = 0
            true
        }

        val searchPlate =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchPlate.hint = "Search"
        val searchPlateView: View =
            searchView.findViewById(androidx.appcompat.R.id.search_plate)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    mainViewModel.searchNews(query)
                    mainViewModel.enableSearch()
                    collectSearchResponse()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }
}