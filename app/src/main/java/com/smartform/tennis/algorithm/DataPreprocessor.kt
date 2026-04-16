package com.smartform.tennis.algorithm

import com.smartform.tennis.algorithm.model.SensorDataPoint
import kotlin.math.sqrt

/**
 * 数据预处理器
 *
 * 负责传感器数据的滤波、去噪、去重力
 */
class DataPreprocessor {

    // 低通滤波系数（用于重力估计）
    private val gravityAlpha = 0.98f

    // 重力向量估计
    private var gravityX = 0f
    private var gravityY = 0f
    private var gravityZ = 9.81f

    // 滑动窗口大小
    private val smoothingWindowSize = 5

    // 数据缓冲
    private val dataBuffer = mutableListOf<SensorDataPoint>()

    /**
     * 预处理单个数据点
     *
     * @param dataPoint 原始传感器数据
     * @return 预处理后的数据
     */
    fun process(dataPoint: SensorDataPoint): SensorDataPoint {
        // 1. 更新重力估计（低通滤波）
        updateGravityEstimate(dataPoint)

        // 2. 去除重力分量
        val motionAccel = removeGravity(dataPoint)

        // 3. 平滑处理（滑动平均）
        val smoothed = applySmoothing(
            dataPoint.copy(
                ax = motionAccel[0],
                ay = motionAccel[1],
                az = motionAccel[2]
            )
        )

        return smoothed
    }

    /**
     * 批量预处理
     */
    fun processBatch(dataPoints: List<SensorDataPoint>): List<SensorDataPoint> {
        return dataPoints.map { process(it) }
    }

    /**
     * 更新重力向量估计（低通滤波）
     *
     * 重力是低频信号，通过低通滤波可以提取
     */
    private fun updateGravityEstimate(dataPoint: SensorDataPoint) {
        gravityX = gravityAlpha * gravityX + (1 - gravityAlpha) * dataPoint.ax
        gravityY = gravityAlpha * gravityY + (1 - gravityAlpha) * dataPoint.ay
        gravityZ = gravityAlpha * gravityZ + (1 - gravityAlpha) * dataPoint.az
    }

    /**
     * 去除重力分量
     *
     * @return [ax_motion, ay_motion, az_motion]
     */
    private fun removeGravity(dataPoint: SensorDataPoint): FloatArray {
        return floatArrayOf(
            dataPoint.ax - gravityX,
            dataPoint.ay - gravityY,
            dataPoint.az - gravityZ
        )
    }

    /**
     * 应用滑动平均平滑
     *
     * 减少高频噪声干扰
     */
    private fun applySmoothing(dataPoint: SensorDataPoint): SensorDataPoint {
        dataBuffer.add(dataPoint)

        // 保持窗口大小
        if (dataBuffer.size > smoothingWindowSize) {
            dataBuffer.removeAt(0)
        }

        // 窗口内数据不足时直接返回
        if (dataBuffer.size < 3) {
            return dataPoint
        }

        // 计算滑动平均
        val size = dataBuffer.size
        var sumAx = 0f
        var sumAy = 0f
        var sumAz = 0f
        var sumGx = 0f
        var sumGy = 0f
        var sumGz = 0f

        for (dp in dataBuffer) {
            sumAx += dp.ax
            sumAy += dp.ay
            sumAz += dp.az
            sumGx += dp.gx
            sumGy += dp.gy
            sumGz += dp.gz
        }

        return SensorDataPoint(
            timestamp = dataPoint.timestamp,
            ax = sumAx / size,
            ay = sumAy / size,
            az = sumAz / size,
            gx = sumGx / size,
            gy = sumGy / size,
            gz = sumGz / size,
            mx = dataPoint.mx,
            my = dataPoint.my,
            mz = dataPoint.mz
        )
    }

    /**
     * 计算合加速度
     */
    fun calculateAccelerationMagnitude(dataPoint: SensorDataPoint): Float {
        return sqrt(dataPoint.ax * dataPoint.ax +
                    dataPoint.ay * dataPoint.ay +
                    dataPoint.az * dataPoint.az)
    }

    /**
     * 计算合角速度
     */
    fun calculateAngularVelocityMagnitude(dataPoint: SensorDataPoint): Float {
        return sqrt(dataPoint.gx * dataPoint.gx +
                    dataPoint.gy * dataPoint.gy +
                    dataPoint.gz * dataPoint.gz)
    }

    /**
     * 重置处理器状态
     */
    fun reset() {
        gravityX = 0f
        gravityY = 0f
        gravityZ = 9.81f
        dataBuffer.clear()
    }
}
