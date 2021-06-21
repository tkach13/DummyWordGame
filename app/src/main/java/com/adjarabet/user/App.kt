package com.adjarabet.user

import android.app.Application
import com.adjarabet.user.common.koinModules.validatorsModule
import com.adjarabet.user.common.koinModules.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App :Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(listOf(viewModelsModule, validatorsModule))
        }
    }
}