package com.smartform.tennis.algorithm.model

/**
 * 动作检测状态机状态
 */
enum class DetectionState {
    IDLE,           // 静止状态，持续监测
    PRE_TRIGGER,    // 预触发缓冲
    TRIGGERED,      // 动作已触发
    RECOVERY        // 恢复期，等待静止
}

/**
 * 动作检测结果
 */
data class DetectionResult(
    val isSwingDetected: Boolean,
    val dataWindow: List<SensorDataPoint>? = null,
    val startTime: Long? = null,
    val endTime: Long? = null
)
