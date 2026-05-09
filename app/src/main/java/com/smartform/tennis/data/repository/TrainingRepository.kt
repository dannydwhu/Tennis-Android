package com.smartform.tennis.data.repository

import com.smartform.tennis.data.model.TrainingSession
import com.smartform.tennis.data.network.ApiClient
import com.smartform.tennis.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 训练数据仓库 - 处理训练相关的 API 调用
 */
class TrainingRepository(
    private val apiService: ApiService = ApiClient().apiService
) {

    /**
     * 开始训练会话
     */
    suspend fun startTraining(userId: Long): Result<TrainingSession> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.startTraining(userId)
            if (response.isSuccessful && response.body()?.code == 0) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to start training"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 结束训练会话
     */
    suspend fun endTraining(sessionId: Long): Result<TrainingSession> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.endTraining(sessionId)
            if (response.isSuccessful && response.body()?.code == 0) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to end training"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 记录单次击球
     */
    suspend fun recordShot(
        sessionId: Long,
        userId: Long,
        shotType: String,
        maxSpeed: Double
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.recordShot(sessionId, userId, shotType, maxSpeed)
            if (response.isSuccessful && response.body()?.code == 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to record shot"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 批量记录击球
     */
    suspend fun recordShots(
        sessionId: Long,
        userId: Long,
        shots: List<ShotData>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 逐条记录击球
            shots.forEach { shot ->
                val response = apiService.recordShot(sessionId, userId, shot.shotType, shot.maxSpeed)
                if (!response.isSuccessful || response.body()?.code != 0) {
                    return@withContext Result.failure(Exception("Failed to record shot: ${shot.shotType}"))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取训练历史
     */
    suspend fun getTrainingSessions(userId: Long): Result<List<TrainingSession>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTrainingSessions(userId)
            if (response.isSuccessful && response.body()?.code == 0) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to get sessions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 上传训练数据（结束后一次性上传所有击球）
     */
    suspend fun uploadTrainingData(
        sessionId: Long,
        userId: Long,
        shots: List<ShotData>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            recordShots(sessionId, userId, shots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 击球数据
     */
    data class ShotData(
        val shotType: String,
        val maxSpeed: Double
    )
}
