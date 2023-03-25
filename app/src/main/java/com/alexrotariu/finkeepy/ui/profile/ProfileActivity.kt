package com.alexrotariu.finkeepy.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.databinding.ActivityProfileBinding
import com.alexrotariu.finkeepy.ui.notifications.ProfileViewModel
import javax.inject.Inject


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    @Inject
    lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}