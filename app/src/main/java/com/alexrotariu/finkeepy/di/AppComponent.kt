package com.alexrotariu.finkeepy.di

import com.alexrotariu.finkeepy.ui.main.MainActivity
import com.alexrotariu.finkeepy.ui.main.charts.ChartsFragment
import com.alexrotariu.finkeepy.ui.notifications.NotificationsActivity
import com.alexrotariu.finkeepy.ui.profile.ProfileActivity
import com.alexrotariu.finkeepy.ui.settings.SettingsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, FirebaseModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: SettingsActivity)
    fun inject(activity: NotificationsActivity)
    fun inject(activity: ProfileActivity)
    fun inject(fragment: ChartsFragment)
}
