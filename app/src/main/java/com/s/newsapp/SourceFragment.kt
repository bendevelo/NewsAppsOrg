package com.s.newsapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.s.newsapp.Adapter.SourceAdapter
import com.s.newsapp.base.BaseFragment
import com.s.newsapp.databinding.FragmentSourceBinding
import com.s.newsapp.remote.NetworkState
import com.s.newsapp.utils.Constants
import com.s.newsapp.utils.EndlessRecyclerOnScrollListener
import com.s.newsapp.utils.EspressoIdlingResource
import com.s.newsapp.viewmodel.CategoryViewModel


class SourceFragment : BaseFragment<FragmentSourceBinding>() {
    override fun setBinding(): FragmentSourceBinding =
        FragmentSourceBinding.inflate(layoutInflater)

    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener
    lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: SourceAdapter
    private lateinit var searchView: SearchView
    val args: SourceFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val source = args.source.category
        categoryViewModel = (activity as MainActivity).categoryViewModel
        setupUI(source)
        setupRecyclerView()
        setupObservers()
        setHasOptionsMenu(true)
        categoryViewModel.clearSearch()
        categoryViewModel.fetchSource(source)


    }




    private fun setupUI(source :String) {

        EspressoIdlingResource.increment()
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            if (categoryViewModel.searchEnable) {
//                mainViewModel.searchNews(mainViewModel.newQuery)
            } else {

            }
            hideErrorMessage()
        }

        onScrollListener = object : EndlessRecyclerOnScrollListener(Constants.QUERY_PER_PAGE) {
            override fun onLoadMore() {
                if (categoryViewModel.searchEnable) {
//                    mainViewModel.searchNews(mainViewModel.newQuery)
                } else {
                    categoryViewModel.fetchSource(source)
                }
            }
        }
        //Swipe refresh listener
        val refreshListener = SwipeRefreshLayout.OnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            categoryViewModel.clearSearch()
            categoryViewModel.fetchSource(source)
        }
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener)


        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                categoryAdapter.getFilter().filter(newText)
                return false
            }
        })

    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.errorCard.visibility = View.GONE
        onScrollListener.isError = false
    }

    private fun setupRecyclerView() {
        categoryAdapter = SourceAdapter()
        binding.rvSource.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(onScrollListener)
        }
        categoryAdapter.setOnItemClickListener { news ->
            val bundle = Bundle().apply {
                putSerializable("source", news)
            }
            findNavController().navigate(
                R.id.categoryFragment,
                bundle
            )
        }


    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            if (!categoryViewModel.searchEnable) {
                categoryViewModel.sourceResponse.collect { response ->
                    when (response) {
                        is NetworkState.Success -> {
                            hideProgressBar()
                            hideErrorMessage()
                            response.data?.let { newResponse ->
                                EspressoIdlingResource.decrement()

                                Log.d("cekdata","newResponse.sources.size.toString()")

                                categoryAdapter.differ.submitList(newResponse.sources.toList())
                                categoryViewModel.totalPage =
                                    newResponse.sources.size / Constants.QUERY_PER_PAGE + 1
                                onScrollListener.isLastPage =
                                    categoryViewModel.feedNewsPage == categoryViewModel.totalPage + 1
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

                        else -> {
                            Log.d("cekdata",response.message.toString())
                        }
                    }
                }
            } else {
//                collectSearchResponse()
            }
        }

        lifecycleScope.launchWhenStarted {
            categoryViewModel.errorMessage.collect { value ->
                if (value.isNotEmpty()) {
                    Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
                }
                categoryViewModel.hideErrorToast()
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun hideBottomPadding() {
        if (onScrollListener.isLastPage) {
            binding.rvSource.setPadding(0, 0, 0, 0)
        }
    }
    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.errorCard.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = message
        onScrollListener.isError = true
    }

}