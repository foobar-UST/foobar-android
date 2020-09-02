package com.foobarust.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by kevin on 8/26/20
 */

@HiltAndroidApp
class FoobarUST : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}