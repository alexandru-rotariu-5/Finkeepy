package com.alexrotariu.finkeepy

import android.app.Application
import com.alexrotariu.finkeepy.di.AppComponent
import com.alexrotariu.finkeepy.di.AppModule
import com.alexrotariu.finkeepy.di.DaggerAppComponent

class App: Application() {
    lateinit var appComponent: AppComponent

    private fun initDagger(app: App): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)
    }


}