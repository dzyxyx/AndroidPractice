package com.example.androidpractice

import android.app.Application
import com.example.androidpractice.data.local.AppDatabase
import com.example.androidpractice.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidPracticeApp : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AndroidPracticeApp)
            modules(appModule)
        }
    }
}
