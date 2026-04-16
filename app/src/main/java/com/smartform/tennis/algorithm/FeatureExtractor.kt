package com.smartform.tennis.algorithm

import com.smartform.tennis.algorithm.model.SensorDataPoint
import com.smartform.tennis.algorithm.model.SwingType
import kotlin.math.*

/**
 * 特征提取器
 *
 * 从动作窗口数据中提取用于分类的特征
 */
class FeatureExtractor {

    /**
     * 提取动作特征
     */
    fun extractFeatures(dataWindow: List<SensorDataPoint>): SwingFeatures {
        if (dataWindow.isEmpty()) {
            return SwingFeatures.INVALID
        }

        val duration = dataWindow.last().timestamp - dataWindow.first().timestamp

        return SwingFeatures(
            // 时域特征
            meanAcceleration = mean(dataWindow.map { it.accelerationMagnitude }),
            maxAcceleration = dataWindow.maxOf { it.accelerationMagnitude },
            minAcceleration = dataWindow.minOf { it.accelerationMagnitude },
            stdAcceleration = stdDev(dataWindow.map { it.accelerationMagnitude }),

            meanAngularVelocity = mean(dataWindow.map { it.angularVelocityMagnitude }),
            maxAngularVelocity = dataWindow.maxOf { it.angularVelocityMagnitude },
            stdAngularVelocity = stdDev(dataWindow.map { it.angularVelocityMagnitude }),

            // 波形特征
            kurtosis = kurtosis(dataWindow.map { it.accelerationMagnitude }),
            skewness = skewness(dataWindow.map { it.accelerationMagnitude }),
            zeroCrossingRate = zeroCrossingRate(dataWindow.map { it.ax }),

            // 旋转方向特征
            rotationDirection = detectRotationDirection(dataWindow),
            rotationAngle = estimateRotationAngle(dataWindow),

            // 高度特征
            avgAz = mean(dataWindow.map { it.az }),
            maxAz = dataWindow.maxOf { it.az },

            // 时间特征
            durationMs = duration,

            // 原始数据
            dataWindow = dataWindow
        )
    }

    /**
     * 检测旋转方向
     *
     * 通过分析 gy 和 gz 的积分判断旋转方向
     * 正手：顺时针（从上方看）
     * 反手：逆时针
     */
    private fun detectRotationDirection(dataWindow: List<SensorDataPoint>): RotationDirection {
        if (dataWindow.size < 2) return RotationDirection.UNKNOWN

        val dt = (dataWindow[1].timestamp - dataWindow[0].timestamp) / 1000.0  // 转换为秒

        // 积分得到角度变化
        var totalRotationY = 0.0
        var totalRotationZ = 0.0

        for (i in 1 until dataWindow.size) {
            totalRotationY += dataWindow[i].gy * dt
            totalRotationZ += dataWindow[i].gz * dt
        }

        // 综合判断
        val netRotation = totalRotationY + totalRotationZ

        return when {
            netRotation > 0.5 -> RotationDirection.CLOCKWISE      // 顺时针
            netRotation < -0.5 -> RotationDirection.COUNTER_CLOCKWISE  // 逆时针
            else -> RotationDirection.UNKNOWN
        }
    }

    /**
     * 估算旋转角度（弧度）
     */
    private fun estimateRotationAngle(dataWindow: List<SensorDataPoint>): Float {
        if (dataWindow.size < 2) return 0f

        val dt = (dataWindow[1].timestamp - dataWindow[0].timestamp) / 1000.0

        var totalAngle = 0.0
        for (i in 1 until dataWindow.size) {
            val angularMag = sqrt(
                dataWindow[i].gx * dataWindow[i].gx +
                dataWindow[i].gy * dataWindow[i].gy +
                dataWindow[i].gz * dataWindow[i].gz
            )
            totalAngle += angularMag * dt
        }

        return totalAngle.toFloat()
    }

    /**
     * 计算均值
     */
    private fun mean(values: List<Float>): Float {
        if (values.isEmpty()) return 0f
        return values.sum() / values.size
    }

    /**
     * 计算标准差
     */
    private fun stdDev(values: List<Float>): Float {
        if (values.size < 2) return 0f
        val mean = mean(values)
        val variance = values.sumOf { (it - mean).toDouble() }.pow(2) / values.size
        return sqrt(variance).toFloat()
    }

    /**
     * 计算峰度（描述波形的尖锐程度）
     */
    private fun kurtosis(values: List<Float>): Float {
        if (values.size < 4) return 0f
        val mean = mean(values)
        val std = stdDev(values)
        if (std < 0.001f) return 0f

        val n = values.size
        var sum = 0.0
        for (v in values) {
            sum += ((v - mean) / std).pow(4)
        }

        return (sum / n - 3).toFloat()  // 超额峰度
    }

    /**
     * 计算偏度（描述波形的不对称性）
     */
    private fun skewness(values: List<Float>): Float {
        if (values.size < 3) return 0f
        val mean = mean(values)
        val std = stdDev(values)
        if (std < 0.001f) return 0f

        val n = values.size
        var sum = 0.0
        for (v in values) {
            sum += ((v - mean) / std).pow(3)
        }

        return (sum / n).toFloat()
    }

    /**
     * 计算零交叉率
     */
    private fun zeroCrossingRate(values: List<Float>): Float {
        if (values.size < 2) return 0f

        var crossings = 0
        for (i in 1 until values.size) {
            if ((values[i] >= 0 && values[i - 1] < 0) ||
                (values[i] < 0 && values[i - 1] >= 0)) {
                crossings++
            }
        }

        return crossings.toFloat() / values.size
    }
}

/**
 * 动作特征数据类
 */
data class SwingFeatures(
    // 时域特征
    val meanAcceleration: Float,
    val maxAcceleration: Float,
    val minAcceleration: Float,
    val stdAcceleration: Float,

    val meanAngularVelocity: Float,
    val maxAngularVelocity: Float,
    val stdAngularVelocity: Float,

    // 波形特征
    val kurtosis: Float,
    val skewness: Float,
    val zeroCrossingRate: Float,

    // 旋转特征
    val rotationDirection: RotationDirection,
    val rotationAngle: Float,

    // 高度特征
    val avgAz: Float,
    val maxAz: Float,

    // 时间特征
    val durationMs: Long,

    // 原始数据
    val dataWindow: List<SensorDataPoint>
) {
    companion object {
        val INVALID = SwingFeatures(
            meanAcceleration = 0f, maxAcceleration = 0f, minAcceleration = 0f,
            stdAcceleration = 0f, meanAngularVelocity = 0f, maxAngularVelocity = 0f,
            stdAngularVelocity = 0f, kurtosis = 0f, skewness = 0f,
            zeroCrossingRate = 0f, rotationDirection = RotationDirection.UNKNOWN,
            rotationAngle = 0f, avgAz = 0f, maxAz = 0f, durationMs = 0,
            dataWindow = emptyList()
        )
    }
}

/**
 * 旋转方向
 */
enum class RotationDirection {
    CLOCKWISE,           // 顺时针
    COUNTER_CLOCKWISE,   // 逆时针
    UNKNOWN
}
