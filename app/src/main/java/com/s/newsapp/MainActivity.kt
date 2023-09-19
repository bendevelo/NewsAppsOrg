package com.s.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.s.newsapp.base.BaseActivity
import com.s.newsapp.databinding.ActivityMainBinding
import com.s.newsapp.viewmodel.CategoryViewModel
import com.s.newsapp.viewmodel.MainViewModel
import com.s.newsapp.viewmodel.SourceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    val mainViewModel: MainViewModel by viewModels()
    val categoryViewModel: CategoryViewModel by viewModels()
    val sourceViewModel: SourceViewModel by viewModels()
    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Today's News"
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        savedInstanceState?.let {
            mainViewModel.hideErrorToast()
        }
    }

    override fun setBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    private fun setupBottomNavigationBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainFragment ,
                R.id.bookmarkFragment,
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}