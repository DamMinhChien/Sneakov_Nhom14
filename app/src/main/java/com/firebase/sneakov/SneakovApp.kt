package com.firebase.sneakov

import android.app.Application
import com.firebase.sneakov.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SneakovApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SneakovApp)
            modules(appModule)
        }
    }
}