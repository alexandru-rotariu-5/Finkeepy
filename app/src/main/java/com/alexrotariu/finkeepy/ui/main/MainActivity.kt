package com.alexrotariu.finkeepy.ui.main

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
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

    private var isFirstStart = true

    private var currentScreen = Screen.DASHBOARD

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

    private fun showFragmentContainer() {
        binding.navHostFragment.visibility = View.VISIBLE
    }

    private fun setOnClickListeners() {
        binding.bottomMenu.apply {
            btnMenuDashboard.setOnClickListener { onDashboardClicked() }
            btnMenuRecords.setOnClickListener { onRecordsClicked() }
            btnMenuCharts.setOnClickListener { onChartsClicked() }
            btnMenuData.setOnClickListener { onDataClicked() }
            btnMenuSettings.setOnClickListener { onSettingsClicked() }
        }
    }

    private fun onDashboardClicked() {
        if (currentScreen != Screen.DASHBOARD) {
            goToScreen(Screen.DASHBOARD)
        }
    }

    private fun onRecordsClicked() {
        if (currentScreen != Screen.RECORDS) {
            goToScreen(Screen.RECORDS)
        }
    }

    private fun onChartsClicked() {
        if (currentScreen != Screen.CHARTS) {
            goToScreen(Screen.CHARTS)
        }
    }

    private fun onDataClicked() {
        Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun onSettingsClicked() {
        Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun goToScreen(screen: Screen) {
        val fragment = when (screen) {
            Screen.DASHBOARD -> {
                DashboardFragment()
            }
            Screen.RECORDS -> {
                RecordsFragment()
            }
            Screen.CHARTS -> {
                ChartsFragment()
            }
            Screen.DATA -> {
                DashboardFragment()
            }
            Screen.SETTINGS -> {
                DashboardFragment()
            }
        }

        openFragment(fragment)
        updateBottomMenu(screen)
        currentScreen = screen
    }

    private fun updateBottomMenu(screen: Screen) {
        resetMenuButtons()
        when (screen) {
            Screen.DASHBOARD -> {
                binding.bottomMenu.btnMenuDashboard.isSelected = true
                binding.bottomMenu.btnMenuDashboard.isEnabled = false
            }
            Screen.RECORDS -> {
                binding.bottomMenu.btnMenuRecords.isSelected = true
                binding.bottomMenu.btnMenuRecords.isEnabled = false
            }
            Screen.DATA -> {
                binding.bottomMenu.btnMenuData.isSelected = true
                binding.bottomMenu.btnMenuData.isEnabled = false
            }
            Screen.CHARTS -> {
                binding.bottomMenu.btnMenuCharts.isSelected = true
                binding.bottomMenu.btnMenuCharts.isEnabled = false
            }
            Screen.SETTINGS -> {
                binding.bottomMenu.btnMenuSettings.isSelected = true
                binding.bottomMenu.btnMenuSettings.isEnabled = false
            }
        }
    }

    private fun resetMenuButtons() {
        binding.bottomMenu.apply {
            // deselect all buttons
            btnMenuDashboard.isSelected = false
            btnMenuRecords.isSelected = false
            btnMenuCharts.isSelected = false
            btnMenuData.isSelected = false
            btnMenuSettings.isSelected = false
            // enable all buttons
            btnMenuDashboard.isEnabled = true
            btnMenuRecords.isEnabled = true
            btnMenuCharts.isEnabled = true
            btnMenuData.isEnabled = true
            btnMenuSettings.isEnabled = true
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

            if (!isLoading && isFirstStart) {
                isFirstStart = false
                showFragmentContainer()
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