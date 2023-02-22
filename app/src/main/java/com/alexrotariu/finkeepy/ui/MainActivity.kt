package com.alexrotariu.finkeepy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alexrotariu.finkeepy.R
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}