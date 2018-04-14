package com.paralect.citiesquiz

import android.app.Application

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
class App : Application() {

    companion object {
        lateinit var app: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}