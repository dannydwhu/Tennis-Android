package com.smartform.tennis

import android.app.Application

/**
 * Smartform Tennis Application
 *
 * 应用入口 - 简化版本
 */
class TennisApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TennisApplication
            private set
    }
}
