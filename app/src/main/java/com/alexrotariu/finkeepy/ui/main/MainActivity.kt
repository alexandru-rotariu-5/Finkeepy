package com.alexrotariu.finkeepy.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.databinding.ActivityMainBinding
import com.alexrotariu.finkeepy.ui.main.charts.ChartsFragment
import com.alexrotariu.finkeepy.ui.main.dashboard.DashboardFragment
import com.alexrotariu.finkeepy.ui.main.records.RecordsFragment
import com.alexrotariu.finkeepy.ui.models.Screen
import com.alexrotariu.finkeepy.ui.notifications.NotificationsActivity
import com.alexrotariu.finkeepy.ui.profile.ProfileActivity
import com.alexrotariu.finkeepy.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.view.clHeader
import kotlinx.android.synthetic.main.header_main.view.ivNotifications
import kotlinx.android.synthetic.main.header_main.view.ivSettings
import kotlinx.android.synthetic.main.header_main.view.ivUserProfile
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
                    goToScreen(Screen.DASHBOARD)
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
        }

        binding.clContainer.clHeader.ivSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.clContainer.clHeader.ivNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        binding.clContainer.clHeader.ivUserProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
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

    fun goToScreen(screen: Screen) {
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
        }

        openFragment(fragment)
        updateBottomMenu(screen)
        currentScreen = screen
    }

    fun setMainHeaderElevation(elevation: Float) {
        ViewCompat.setElevation(binding.clHeader.root, elevation)
    }

    fun showMainHeaderTitle() {
        binding.clHeader.tvTitle.visibility = View.VISIBLE
    }

    fun hideMainHeaderTitle() {
        binding.clHeader.tvTitle.visibility = View.GONE
    }

    fun setMainHeaderTitle(title: String) {
        binding.clHeader.tvTitle.text = title
    }

    fun setMainHeaderTitleY(y: Float) {
        binding.clHeader.tvTitle.translationY = y
    }

    private fun updateBottomMenu(screen: Screen) {
        resetMenuButtons()

        binding.bottomMenu.apply {
            val button = when (screen) {
                Screen.DASHBOARD -> btnMenuDashboard
                Screen.RECORDS -> btnMenuRecords
                Screen.CHARTS -> btnMenuCharts
            }

            button.isSelected = true
            button.isEnabled = false
        }

    }

    private fun resetMenuButtons() {
        binding.bottomMenu.root.children.forEach { view ->
            if (view is ImageButton) {
                view.isSelected = false
                view.isEnabled = true
            }
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

    private fun openFragment(fragment: Fragment) {
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