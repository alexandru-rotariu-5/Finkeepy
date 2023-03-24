package com.alexrotariu.finkeepy.ui.main

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.databinding.ActivityMainBinding
import com.alexrotariu.finkeepy.ui.main.charts.ChartsFragment
import com.alexrotariu.finkeepy.ui.main.dashboard.DashboardFragment
import com.alexrotariu.finkeepy.ui.main.records.RecordsFragment
import com.alexrotariu.finkeepy.ui.models.Screen
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModel: MainViewModel

    private var isDashboardNetWorthAnimationPlayed = false
    private var isDashboardChartAnimationPlayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        updateBottomMenu(Screen.DASHBOARD)

        setOnClickListeners()
        initObservers()
        setupSwipeRefresh()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
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
        binding.bottomMenu.apply {
            btnMenuDashboard.setOnClickListener { onDashboardClicked() }
            btnMenuRecords.setOnClickListener { onRecordsClicked() }
            btnMenuCharts.setOnClickListener { onChartsClicked() }
        }
    }

    private fun onDashboardClicked() {
        openFragment(DashboardFragment())
        updateBottomMenu(Screen.DASHBOARD)
    }

    private fun onRecordsClicked() {
        openFragment(RecordsFragment())
        updateBottomMenu(Screen.RECORDS)
    }

    private fun onChartsClicked() {
        openFragment(ChartsFragment())
        updateBottomMenu(Screen.CHARTS)
    }

    private fun updateBottomMenu(screen: Screen) {
        deselectAllMenuButtons()
        when (screen) {
            Screen.DASHBOARD -> {
                binding.bottomMenu.btnMenuDashboard.isSelected = true
            }
            Screen.RECORDS -> {
                binding.bottomMenu.btnMenuRecords.isSelected = true
            }
            Screen.DATA -> {
                binding.bottomMenu.btnMenuData.isSelected = true
            }
            Screen.CHARTS -> {
                binding.bottomMenu.btnMenuCharts.isSelected = true
            }
            Screen.SETTINGS -> {
                binding.bottomMenu.btnMenuSettings.isSelected = true
            }
        }
    }

    private fun deselectAllMenuButtons() {
        binding.bottomMenu.apply {
            btnMenuDashboard.isSelected = false
            btnMenuRecords.isSelected = false
            btnMenuCharts.isSelected = false
            btnMenuData.isSelected = false
            btnMenuSettings.isSelected = false
        }
    }

    private fun initObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.duration = 200

            val fadeOut = AlphaAnimation(1f, 0f)
            fadeOut.duration = 200

            if (isLoading) {
                binding.pbLoadingIndicator.startAnimation(fadeIn)
                binding.pbLoadingIndicator.visibility = View.VISIBLE
            } else {
                binding.pbLoadingIndicator.startAnimation(fadeOut)
                binding.pbLoadingIndicator.visibility = View.GONE
            }
        }

    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.primary
            )
        )
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

    fun isDashboardNetWorthAnimationPlayed() = isDashboardNetWorthAnimationPlayed

    fun setDashboardNetWorthAnimationPlayed() {
        isDashboardNetWorthAnimationPlayed = true
    }

    fun isDashboardChartAnimationPlayed() = isDashboardChartAnimationPlayed

    fun setDashboardChartAnimationPlayed() {
        isDashboardChartAnimationPlayed = true
    }
}