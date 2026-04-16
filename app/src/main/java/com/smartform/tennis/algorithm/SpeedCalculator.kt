package com.smartform.tennis.algorithm

import com.smartform.tennis.algorithm.model.SensorDataPoint
import kotlin.math.sqrt

/**
 * 速度计算器
 *
 * 基于加速度积分计算击球速度
 *
 * 算法步骤：
 * 1. 去除重力分量
 * 2. 梯形积分得到速度
 * 3. 取最大值为击球速度
 */
class SpeedCalculator {

    // 重力加速度 (m/s²)
    private val gravity = 9.81f

    /**
     * 计算击球速度
     *
     * @param dataWindow 动作窗口内的传感器数据
     * @return 最大速度和平均速度 (km/h)
     */
    fun calculateSpeed(dataWindow: List<SensorDataPoint>): SpeedResult {
        if (dataWindow.size < 2) {
            return SpeedResult(0f, 0f)
        }

        // 1. 估计重力向量（使用起始数据的平均）
        val gravityVector = estimateGravity(dataWindow.take(10))

        // 2. 去除重力分量，得到纯运动加速度
        val motionAccel = removeGravity(dataWindow, gravityVector)

        // 3. 计算合加速度
        val accelMagnitude = motionAccel.map { sqrt(it[0] * it[0] + it[1] * it[1] + it[2] * it[2]) }

        // 4. 积分得到速度
        val velocities = integrateVelocity(accelMagnitude, dataWindow)

        // 5. 转换为 km/h 并统计
        val speedsKmh = velocities.map { it * 3.6f }  // m/s → km/h

        val maxSpeed = speedsKmh.maxOrNull() ?: 0f
        val avgSpeed = speedsKmh.filter { it > 0 }.average().toFloat()

        return SpeedResult(maxSpeed, avgSpeed)
    }

    /**
     * 估计重力向量
     *
     * 假设动作开始前的数据主要是重力
     */
    private fun estimateGravity(initialData: List<SensorDataPoint>): FloatArray {
        if (initialData.isEmpty()) {
            return floatArrayOf(0f, 0f, gravity)
        }

        var sumX = 0f
        var sumY = 0f
        var sumZ = 0f

        for (d in initialData) {
            sumX += d.ax
            sumY += d.ay
            sumZ += d.az
        }

        val n = initialData.size
        return floatArrayOf(sumX / n, sumY / n, sumZ / n)
    }

    /**
     * 去除重力分量
     *
     * @return 每个数据点的 [ax_motion, ay_motion, az_motion]
     */
    private fun removeGravity(
        dataWindow: List<SensorDataPoint>,
        gravityVector: FloatArray
    ): List<FloatArray> {
        return dataWindow.map { d ->
            floatArrayOf(
                d.ax - gravityVector[0],
                d.ay - gravityVector[1],
                d.az - gravityVector[2]
            )
        }
    }

    /**
     * 梯形积分计算速度
     *
     * v[i] = v[i-1] + (a[i] + a[i-1]) * dt / 2
     */
    private fun integrateVelocity(accelMagnitude: List<Float>, dataWindow: List<SensorDataPoint>): List<Float> {
        val n = accelMagnitude.size
        if (n < 2) return listOf(0f)

        val velocities = mutableListOf<Float>()
        velocities.add(0f)  // 初始速度为 0

        for (i in 1 until n) {
            // 计算时间间隔 (秒)
            val dt = (dataWindow[i].timestamp - dataWindow[i - 1].timestamp) / 1000.0f

            // 梯形积分
            val dv = (accelMagnitude[i] + accelMagnitude[i - 1]) * dt / 2

            val newVelocity = velocities.last() + dv
            velocities.add(newVelocity.coerceAtLeast(0f))  // 速度不为负
        }

        return velocities
    }

    /**
     * 简化的速度估算（基于角速度峰值）
     *
     * 当加速度数据不可靠时，使用角速度进行估算
     * 这是经验公式，需要实际校准
     */
    fun estimateSpeedFromAngularVelocity(dataWindow: List<SensorDataPoint>): Float {
        if (dataWindow.isEmpty()) return 0f

        // 找到角速度峰值
        val maxAngularVel = dataWindow.maxOf { it.angularVelocityMagnitude }

        // 经验公式：速度 ≈ 角速度 × 力臂长度 (假设力臂 0.7m)
        // 然后转换为 km/h
        val estimatedSpeed = maxAngularVel * 0.7f * 3.6f

        return estimatedSpeed
    }

    /**
     * 校准速度计算
     *
     * 使用已知的标准速度来校准算法参数
     */
    fun calibrate(standardSpeedKmh: Float, dataWindow: List<SensorDataPoint>): Float {
        val calculatedSpeed = calculateSpeed(dataWindow).maxSpeed

        if (calculatedSpeed > 0) {
            return standardSpeedKmh / calculatedSpeed  // 返回校准系数
        }

        return 1.0f
    }
}

/**
 * 速度计算结果
 */
data class SpeedResult(
    val maxSpeed: Float,  // 最大速度 (km/h)
    val avgSpeed: Float   // 平均速度 (km/h)
)
