package com.alexrotariu.finkeepy.ui.notifications

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.databinding.ActivityNotificationsBinding
import javax.inject.Inject


class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    @Inject
    lateinit var viewModel: NotificationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}