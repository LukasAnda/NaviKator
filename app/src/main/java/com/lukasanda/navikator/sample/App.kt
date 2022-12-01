package com.lukasanda.navikator.sample

import android.app.Application
import com.lukasanda.navikator.BuildConfig
import com.lukasanda.navikator.sample.di.navigatorModule
import com.lukasanda.navikator.sample.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // This setup is needed as explained in https://github.com/InsertKoinIO/koin/issues/1188
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            val modules = listOf(
                navigatorModule,
                viewModelModule
            )
            modules(modules)
        }
    }
}