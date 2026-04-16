package com.smartform.tennis.algorithm.model

/**
 * 传感器数据点
 *
 * 单个时间点的传感器读数
 */
data class SensorDataPoint(
    val timestamp: Long,          // 时间戳 (ms)
    val ax: Float,                // 加速度 X 轴 (m/s²)
    val ay: Float,                // 加速度 Y 轴 (m/s²)
    val az: Float,                // 加速度 Z 轴 (m/s²)
    val gx: Float,                // 角速度 X 轴 (rad/s)
    val gy: Float,                // 角速度 Y 轴 (rad/s)
    val gz: Float,                // 角速度 Z 轴 (rad/s)
    val mx: Float,                // 磁力计 X 轴 (μT)
    val my: Float,                // 磁力计 Y 轴 (μT)
    val mz: Float                 // 磁力计 Z 轴 (μT)
) {
    /**
     * 计算合加速度 (单位：m/s²)
     */
    val accelerationMagnitude: Float
        get() = kotlin.math.sqrt(ax * ax + ay * ay + az * az)

    /**
     * 计算合角速度 (单位：rad/s)
     */
    val angularVelocityMagnitude: Float
        get() = kotlin.math.sqrt(gx * gx + gy * gy + gz * gz)

    companion object {
        /**
         * 重力加速度 (单位：m/s²)
         */
        const val GRAVITY = 9.81f
    }
}
