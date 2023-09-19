package com.s.newsapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.s.newsapp.Adapter.CategoryAdapter
import com.s.newsapp.Adapter.NewsAdapter
import com.s.newsapp.base.BaseFragment
import com.s.newsapp.data.model.CategoryArticle
import com.s.newsapp.databinding.FragmentCategoryBinding
import com.s.newsapp.databinding.FragmentMainBinding
import com.s.newsapp.remote.NetworkState
import com.s.newsapp.utils.Constants
import com.s.newsapp.utils.EspressoIdlingResource
import com.s.newsapp.viewmodel.CategoryViewModel
import com.s.newsapp.viewmodel.MainViewModel
import java.nio.channels.Selector


class MainFragment : BaseFragment<FragmentMainBinding>() {
    override fun setBinding(): FragmentMainBinding =
        FragmentMainBinding.inflate(layoutInflater)

    private lateinit var categoryAdapter: CategoryAdapter
    lateinit var categoryViewModel: CategoryViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryViewModel = (activity as MainActivity).categoryViewModel
        setupUI()
        setupRecyclerView()
        setupObservers()
        setHasOptionsMenu(true)
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter()
        binding.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(activity)
//            addOnScrollListener(onScrollListener)
        }
        categoryAdapter.setOnItemClickListener { source ->
            val bundle = Bundle().apply {
                putSerializable("source", source)
            }
            findNavController().navigate(
                R.id.sourceFragment,
                bundle
          )
        }
    }


    private fun setupUI() {
        categoryViewModel.fetchCategory(Constants.COUNTRY_CODE)
        val refreshListener = SwipeRefreshLayout.OnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            categoryViewModel.fetchCategory(Constants.COUNTRY_CODE)
        }
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            categoryViewModel.categoryResponse.collect { response ->
                when (response) {
                    is NetworkState.Success -> {
                        hideProgressBar()
                        hideErrorMessage()

                        response.data?.let { newResponse ->
                            EspressoIdlingResource.decrement()

                            categoryAdapter.differ.submitList(
                                newResponse.sources.toMutableSet().toList()
                                    .distinctBy { (it.category) })
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

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.errorCard.visibility = View.GONE
//        onScrollListener.isError = false
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.errorCard.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = message
    }

}



