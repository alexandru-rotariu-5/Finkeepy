package com.alexrotariu.finkeepy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.databinding.ActivityMainBinding
import com.alexrotariu.finkeepy.ui.dashboard.DashboardFragment
import com.alexrotariu.finkeepy.ui.records.RecordsFragment
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.bottomMenu.btnMenuDashboard.setOnClickListener {
            openFragment(DashboardFragment())
        }

        binding.bottomMenu.btnMenuRecords.setOnClickListener {
            openFragment(RecordsFragment())
        }
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}