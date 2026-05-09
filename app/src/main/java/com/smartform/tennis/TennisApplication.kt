package com.smartform.tennis

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

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

        private const val PREFS_NAME = "tennis_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"

        private val prefs: SharedPreferences by lazy {
            instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        var userId: Long
            get() = prefs.getLong(KEY_USER_ID, -1)
            set(value) = prefs.edit().putLong(KEY_USER_ID, value).apply()

        var accessToken: String?
            get() = prefs.getString(KEY_ACCESS_TOKEN, null)
            set(value) = prefs.edit().putString(KEY_ACCESS_TOKEN, value).apply()

        var refreshToken: String?
            get() = prefs.getString(KEY_REFRESH_TOKEN, null)
            set(value) = prefs.edit().putString(KEY_REFRESH_TOKEN, value).apply()

        var isLoggedIn: Boolean
            get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
            set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

        fun saveSession(userId: Long, accessToken: String, refreshToken: String) {
            this.userId = userId
            this.accessToken = accessToken
            this.refreshToken = refreshToken
            this.isLoggedIn = true
        }

        fun clearSession() {
            prefs.edit().clear().apply()
        }

        fun getAuthHeader(): String? {
            return accessToken?.let { "Bearer $it" }
        }
    }
}
