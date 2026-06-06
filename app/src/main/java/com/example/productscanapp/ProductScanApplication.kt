package com.example.productscanapp

import android.app.Application
import com.example.productscanapp.data.sync.FavoritesSyncScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ProductScanApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FavoritesSyncScheduler.scheduleDailySync(this)
    }
}
