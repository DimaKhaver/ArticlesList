package com.example.articleslist

import android.app.Application
import com.example.articleslist.data.remote.network.ConnectivityStateHolder.registerConnectivityBroadcaster
import com.example.articleslist.di.appGlobalModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class ArticlesListApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerConnectivityBroadcaster()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ArticlesListApplication)
            modules(appGlobalModule)
        }
    }
}