package com.smartform.tennis.data.network

import com.smartform.tennis.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API 服务接口
 */
interface ApiService {

    // ==================== 传感器数据 ====================

    /**
     * 上传单条传感器数据
     */
    @POST("/api/sensor/data")
    suspend fun uploadSensorData(@Body data: SensorData): Response<ApiResponse<SensorData>>

    /**
     * 批量上传传感器数据
     */
    @POST("/api/sensor/batch")
    suspend fun uploadBatchSensorData(@Body dataList: List<SensorData>): Response<ApiResponse<List<SensorData>>>

    // ==================== 训练数据 ====================

    /**
     * 开始训练
     */
    @POST("/api/training/start")
    suspend fun startTraining(@Query("userId") userId: Long): Response<ApiResponse<TrainingSession>>

    /**
     * 结束训练
     */
    @POST("/api/training/end")
    suspend fun endTraining(@Query("sessionId") sessionId: Long): Response<ApiResponse<TrainingSession>>

    /**
     * 记录击球
     */
    @POST("/api/training/shots")
    suspend fun recordShot(
        @Query("sessionId") sessionId: Long,
        @Query("userId") userId: Long,
        @Query("shotType") shotType: String,
        @Query("maxSpeed") maxSpeed: Double
    ): Response<ApiResponse<Shot>>

    /**
     * 获取训练历史
     */
    @GET("/api/training/sessions")
    suspend fun getTrainingSessions(@Query("userId") userId: Long): Response<ApiResponse<List<TrainingSession>>>

    /**
     * 获取训练报告
     */
    @GET("/api/training/{sessionId}/report")
    suspend fun getTrainingReport(@Path("sessionId") sessionId: Long): Response<ApiResponse<Map<String, Any>>>

    // ==================== 用户相关 ====================

    /**
     * 获取用户信息
     */
    @GET("/api/users/{userId}")
    suspend fun getUser(@Path("userId") userId: Long): Response<ApiResponse<User>>

    /**
     * 更新用户信息
     */
    @PUT("/api/users/{userId}")
    suspend fun updateUser(@Path("userId") userId: Long, @Body user: User): Response<ApiResponse<User>>
}
