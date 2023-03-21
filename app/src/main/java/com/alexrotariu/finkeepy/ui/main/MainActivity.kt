package com.alexrotariu.finkeepy.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.databinding.ActivityMainBinding
import com.alexrotariu.finkeepy.ui.main.dashboard.DashboardFragment
import com.alexrotariu.finkeepy.ui.main.charts.ChartsFragment
import com.alexrotariu.finkeepy.ui.main.records.RecordsFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setOnClickListeners()
        initObservers()
        setupSwipeRefresh()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                if (currentFragment is DashboardFragment) {
                    finish()
                } else {
                    openFragment(DashboardFragment())
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setOnClickListeners() {
        binding.bottomMenu.btnMenuDashboard.setOnClickListener {
            openFragment(DashboardFragment())
        }

        binding.bottomMenu.btnMenuRecords.setOnClickListener {
            openFragment(RecordsFragment())
        }

        binding.bottomMenu.btnMenuCharts.setOnClickListener {
            openFragment(ChartsFragment())
        }
    }

    private fun initObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.pbLoadingIndicator.visibility = View.VISIBLE
            } else {
                binding.pbLoadingIndicator.visibility = View.GONE
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.primary))
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getRecords {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}