package com.alexrotariu.finkeepy.di

import com.alexrotariu.finkeepy.ui.main.MainActivity
import com.alexrotariu.finkeepy.ui.main.charts.ChartsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, FirebaseModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: ChartsFragment)
}
