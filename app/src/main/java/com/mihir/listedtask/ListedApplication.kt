package com.mihir.listedtask

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.mihir.listedtask.common.AppObjectController

class ListedApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppObjectController.applicationContext = this
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}