package com.smartform.tennis.data.network

import com.smartform.tennis.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API 服务接口
 */
interface ApiService {

    // ==================== 用户认证 ====================

    /**
     * 用户注册
     */
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    /**
     * 用户登录
     */
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    /**
     * 刷新令牌
     */
    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Query("refreshToken") refreshToken: String): Response<ApiResponse<TokenPair>>

    /**
     * 获取当前用户信息
     */
    @GET("/api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") authorization: String): Response<ApiResponse<User>>

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
     * 获取用户资料
     */
    @GET("/api/users/profile")
    suspend fun getProfile(@Query("userId") userId: Long): Response<ApiResponse<User>>

    /**
     * 获取用户统计数据
     */
    @GET("/api/users/stats")
    suspend fun getUserStats(
        @Query("userId") userId: Long,
        @Query("timeRange") timeRange: String = "week"
    ): Response<ApiResponse<Map<String, Any>>>

    /**
     * 更新用户信息
     */
    @PUT("/api/users/{userId}")
    suspend fun updateUser(@Path("userId") userId: Long, @Body user: User): Response<ApiResponse<User>>

    // ==================== 排行榜 ====================

    /**
     * 总击球数排行榜
     */
    @GET("/api/leaderboard/total-shots")
    suspend fun getTotalShotsLeaderboard(
        @Query("timeRange") timeRange: String = "week",
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<LeaderboardEntry>>>

    /**
     * 分类型击球数排行榜
     */
    @GET("/api/leaderboard/by-shot-type")
    suspend fun getByShotTypeLeaderboard(
        @Query("shotType") shotType: String,
        @Query("timeRange") timeRange: String = "week",
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<LeaderboardEntry>>>

    /**
     * 最大速度排行榜
     */
    @GET("/api/leaderboard/max-speed")
    suspend fun getMaxSpeedLeaderboard(
        @Query("timeRange") timeRange: String = "week",
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<LeaderboardEntry>>>

    /**
     * 获取用户个人排名
     */
    @GET("/api/leaderboard/my-rank")
    suspend fun getMyRank(
        @Query("userId") userId: Long,
        @Query("timeRange") timeRange: String = "week"
    ): Response<ApiResponse<MyRankResponse>>
}
