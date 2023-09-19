package com.s.newsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.core.view.isGone
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.s.newsapp.base.BaseFragment
import com.s.newsapp.databinding.FragmentDetailedBinding
import com.s.newsapp.viewmodel.MainViewModel


class DetailedFragment : BaseFragment<FragmentDetailedBinding>() {

    override fun setBinding(): FragmentDetailedBinding =
        FragmentDetailedBinding.inflate(layoutInflater)

    private lateinit var viewModel: MainViewModel
    private val args: DetailedFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).mainViewModel
        setupUI(view)
        setupObserver()
    }

    private fun setupUI(view: View) {
        val news = args.news
        binding.webView.apply {
            webViewClient = WebViewClient()
            news.url?.let {
                loadUrl(it)
            }
        }

        binding.fab.setOnClickListener {
            viewModel.saveNews(news)
            Snackbar.make(view, "News article added to favorites.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupObserver() {
        viewModel.getFavoriteNews().observe(viewLifecycleOwner) { news ->
            binding.fab.isGone = news.any { it.title == args.news.title }
        }
    }

}