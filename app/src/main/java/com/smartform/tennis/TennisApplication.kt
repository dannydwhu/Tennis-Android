package com.smartform.tennis

import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import com.smartform.tennis.data.local.AppDatabase
import com.smartform.tennis.data.network.ApiClient
import com.smartform.tennis.data.repository.TennisRepository

/**
 * Smartform Tennis Application
 *
 * 应用入口，初始化全局单例
 */
class TennisApplication : Application() {

    // 数据库
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    // API 客户端
    val apiClient: ApiClient by lazy { ApiClient() }

    // 仓库
    val repository: TennisRepository by lazy { TennisRepository(apiClient, database) }

    // 蓝牙管理器
    val bluetoothManager: BluetoothManager by lazy {
        getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TennisApplication
            private set
    }
}
