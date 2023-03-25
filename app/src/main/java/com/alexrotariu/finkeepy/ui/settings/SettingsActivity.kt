package com.alexrotariu.finkeepy.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.databinding.ActivitySettingsBinding
import javax.inject.Inject


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    @Inject
    lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}