package com.smartform.tennis.algorithm.model

/**
 * 击球事件
 *
 * 识别出的完整击球动作
 */
data class SwingEvent(
    val swingType: SwingType,     // 击球类型
    val maxSpeed: Float,          // 最大速度 (km/h)
    val avgSpeed: Float,          // 平均速度 (km/h)
    val startTime: Long,          // 开始时间戳
    val endTime: Long,            // 结束时间戳
    val duration: Long,           // 动作时长 (ms)
    val qualityScore: Float,      // 质量评分 (0-100)
    val confidence: Float,        // 识别置信度 (0-1)
    val dataPoints: List<SensorDataPoint>,  // 原始数据段
    val aiSuggestion: String? = null  // AI 建议
) {
    /**
     * 转换为可存储的格式
     */
    fun toShotData(sessionId: Long, userId: Long): ShotData {
        return ShotData(
            sessionId = sessionId,
            userId = userId,
            shotType = swingType,
            maxSpeed = maxSpeed,
            avgSpeed = avgSpeed,
            timestamp = startTime,
            qualityScore = qualityScore.toInt(),
            confidenceScore = confidence,
            aiSuggestion = aiSuggestion,
            swingDurationMs = duration.toInt()
        )
    }
}

/**
 * 击球数据（用于存储）
 */
data class ShotData(
    val sessionId: Long,
    val userId: Long,
    val shotType: SwingType,
    val maxSpeed: Float,
    val avgSpeed: Float,
    val timestamp: Long,
    val qualityScore: Int,
    val confidenceScore: Float,
    val aiSuggestion: String?,
    val swingDurationMs: Int
)
