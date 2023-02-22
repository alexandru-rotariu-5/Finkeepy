package com.alexrotariu.finkeepy.di

import com.alexrotariu.finkeepy.ui.dashboard.DashboardFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, FirebaseModule::class])
interface AppComponent {
    fun inject(target: DashboardFragment)
}
