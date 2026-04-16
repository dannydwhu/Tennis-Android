package com.smartform.tennis.data.local

import android.content.Context
import com.smartform.tennis.data.local.dao.*
import com.smartform.tennis.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 数据同步服务
 *
 * 负责本地数据与服务器之间的同步
 */
class DataSyncService(
    private val context: Context,
    private val database: AppDatabase
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val sessionDao = database.trainingSessionDao()
    private val shotDao = database.shotDao()
    private val sensorDataDao = database.sensorDataDao()

    // 同步状态
    private var isSyncing = false

    /**
     * 开始同步
     *
     * @param apiService 网络 API 服务
     * @param userId 用户 ID
     */
    fun startSync(apiService: Any, userId: Long) {
        if (isSyncing) return

        isSyncing = true

        scope.launch {
            try {
                // 1. 同步训练会话
                syncTrainingSessions(apiService, userId)

                // 2. 同步击球记录
                syncShots(apiService, userId)

                // 3. 同步传感器原始数据
                syncSensorData(apiService)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSyncing = false
            }
        }
    }

    /**
     * 同步训练会话
     */
    private suspend fun syncTrainingSessions(apiService: Any, userId: Long) {
        val unsyncedSessions = sessionDao.getUnsyncedSessions()

        for (session in unsyncedSessions) {
            try {
                // TODO: 调用 API 上传数据
                // apiService.uploadTrainingSession(session.toDto())

                // 标记为已同步
                sessionDao.markAsSynced(session.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 同步击球记录
     */
    private suspend fun syncShots(apiService: Any, userId: Long) {
        val unsyncedShots = shotDao.getUnsyncedShots()

        for (shot in unsyncedShots) {
            try {
                // TODO: 调用 API 上传数据
                // apiService.uploadShot(shot.toDto())

                // 标记为已同步
                shotDao.markAsSynced(shot.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 同步传感器原始数据
     */
    private suspend fun syncSensorData(apiService: Any) {
        val unsyncedData = sensorDataDao.getUnsyncedData()

        if (unsyncedData.isEmpty()) return

        try {
            // TODO: 调用 API 批量上传
            // apiService.uploadBatchSensorData(unsyncedData.map { it.toDto() })

            // 标记为已同步
            for (data in unsyncedData) {
                sensorDataDao.markAsSynced(data.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止同步
     */
    fun stopSync() {
        isSyncing = false
    }

    /**
     * 清理旧数据（保留最近 30 天）
     */
    fun cleanupOldData() {
        scope.launch {
            val cutoffTime = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000)  // 30 天前
            sensorDataDao.deleteOldData(cutoffTime)
        }
    }
}
