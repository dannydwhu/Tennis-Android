package com.smartform.tennis.data.repository

import com.smartform.tennis.data.local.AppDatabase
import com.smartform.tennis.data.model.*
import com.smartform.tennis.data.network.ApiClient

/**
 * 数据仓库
 *
 * 统一管理本地和网络数据源
 */
class TennisRepository(
    private val apiClient: ApiClient,
    private val database: AppDatabase
) {

    private val apiService = apiClient.apiService

    // ==================== 传感器数据 ====================

    suspend fun uploadSensorData(data: SensorData): Result<SensorData> {
        return try {
            val response = apiService.uploadSensorData(data)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Upload failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadBatchSensorData(dataList: List<SensorData>): Result<List<SensorData>> {
        return try {
            val response = apiService.uploadBatchSensorData(dataList)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Batch upload failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== 训练数据 ====================

    suspend fun startTraining(userId: Long): Result<TrainingSession> {
        return try {
            val response = apiService.startTraining(userId)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Failed to start training: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun endTraining(sessionId: Long): Result<TrainingSession> {
        return try {
            val response = apiService.endTraining(sessionId)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Failed to end training: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordShot(
        sessionId: Long,
        userId: Long,
        shotType: Shot.ShotType,
        maxSpeed: Double
    ): Result<Shot> {
        return try {
            val response = apiService.recordShot(
                sessionId,
                userId,
                shotType.name,
                maxSpeed
            )
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Failed to record shot: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrainingSessions(userId: Long): Result<List<TrainingSession>> {
        return try {
            val response = apiService.getTrainingSessions(userId)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Failed to get sessions: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrainingReport(sessionId: Long): Result<Map<String, Any>> {
        return try {
            val response = apiService.getTrainingReport(sessionId)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Failed to get report: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
