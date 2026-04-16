package com.smartform.tennis.algorithm.test

import com.smartform.tennis.algorithm.model.SensorDataPoint
import kotlin.math.*
import kotlin.random.Random

/**
 * 模拟传感器数据生成器
 *
 * 生成 6 种击球类型的模拟数据用于算法测试
 */
class MockSensorDataGenerator {

    private val random = Random

    /**
     * 生成正手击球模拟数据
     * 特征：顺时针旋转，腰部高度，尖锐脉冲
     */
    fun generateForehand(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()
        val duration = 45  // 45 个数据点，约 0.45 秒

        for (i in 0 until duration) {
            val t = i / 100.0  // 时间（秒）
            val timestamp = startTime + i * 10

            // 正手特征：Y 轴角速度为主，顺时针旋转
            val gx = (sin(t * PI * 4) * 0.3 + random.nextFloat(-0.1, 0.1)).toFloat()
            val gy = (exp(-((t - 0.2) * 10).pow(2)) * 5.0 + random.nextFloat(-0.2, 0.2)).toFloat()
            val gz = (cos(t * PI * 2) * 0.5 + random.nextFloat(-0.1, 0.1)).toFloat()

            // 加速度：击球瞬间有尖峰
            val peak = exp(-((t - 0.25) * 15).pow(2)) * 15.0
            val ax = (peak * 0.3 + 9.81 * 0.1 + random.nextFloat(-0.5, 0.5)).toFloat()
            val ay = (peak * 0.5 + random.nextFloat(-0.5, 0.5)).toFloat()
            val az = (peak * 0.2 - 9.81 + random.nextFloat(-0.5, 0.5)).toFloat()

            // 磁力计（模拟环境磁场）
            val mx = (20.0 + random.nextFloat(-1, 1)).toFloat()
            val my = (30.0 + random.nextFloat(-1, 1)).toFloat()
            val mz = (40.0 + random.nextFloat(-1, 1)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = mx, my = my, mz = mz
                )
            )
        }

        return dataPoints
    }

    /**
     * 生成反手击球模拟数据
     * 特征：逆时针旋转，腰部高度，尖锐脉冲
     */
    fun generateBackhand(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()
        val duration = 45

        for (i in 0 until duration) {
            val t = i / 100.0
            val timestamp = startTime + i * 10

            // 反手特征：逆时针旋转（负值）
            val gx = (sin(t * PI * 4) * 0.3 + random.nextFloat(-0.1, 0.1)).toFloat()
            val gy = (-exp(-((t - 0.2) * 10).pow(2)) * 5.0 + random.nextFloat(-0.2, 0.2)).toFloat()
            val gz = (-cos(t * PI * 2) * 0.5 + random.nextFloat(-0.1, 0.1)).toFloat()

            // 加速度模式
            val peak = exp(-((t - 0.25) * 15).pow(2)) * 12.0
            val ax = (peak * 0.3 + 9.81 * 0.1 + random.nextFloat(-0.5, 0.5)).toFloat()
            val ay = (peak * 0.4 + random.nextFloat(-0.5, 0.5)).toFloat()
            val az = (peak * 0.2 - 9.81 + random.nextFloat(-0.5, 0.5)).toFloat()

            val mx = (20.0 + random.nextFloat(-1, 1)).toFloat()
            val my = (30.0 + random.nextFloat(-1, 1)).toFloat()
            val mz = (40.0 + random.nextFloat(-1, 1)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = mx, my = my, mz = mz
                )
            )
        }

        return dataPoints
    }

    /**
     * 生成切削模拟数据
     * 特征：波形平缓，旋转较慢，时长较长
     */
    fun generateSlice(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()
        val duration = 55

        for (i in 0 until duration) {
            val t = i / 100.0
            val timestamp = startTime + i * 10

            // 切削特征：旋转平缓，无明显尖峰
            val gx = (sin(t * PI * 2) * 0.5 + random.nextFloat(-0.1, 0.1)).toFloat()
            val gy = (sin(t * PI) * 2.0 + random.nextFloat(-0.2, 0.2)).toFloat()
            val gz = (cos(t * PI) * 1.0 + random.nextFloat(-0.1, 0.1)).toFloat()

            // 加速度：无明显峰值
            val ax = (sin(t * PI * 2) * 3.0 + random.nextFloat(-0.5, 0.5)).toFloat()
            val ay = (cos(t * PI) * 2.0 + random.nextFloat(-0.5, 0.5)).toFloat()
            val az = (-9.81 + sin(t * PI) * 2.0 + random.nextFloat(-0.5, 0.5)).toFloat()

            val mx = (20.0 + random.nextFloat(-1, 1)).toFloat()
            val my = (30.0 + random.nextFloat(-1, 1)).toFloat()
            val mz = (40.0 + random.nextFloat(-1, 1)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = mx, my = my, mz = mz
                )
            )
        }

        return dataPoints
    }

    /**
     * 生成发球/高压模拟数据
     * 特征：时长长，旋转角度大，az 向上
     */
    fun generateServe(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()
        val duration = 75

        for (i in 0 until duration) {
            val t = i / 100.0
            val timestamp = startTime + i * 10

            // 发球特征：大幅度旋转，向上动作
            val gx = (sin(t * PI * 2) * 1.0 + random.nextFloat(-0.2, 0.2)).toFloat()
            val gy = (exp(-((t - 0.4) * 5).pow(2)) * 8.0 + random.nextFloat(-0.3, 0.3)).toFloat()
            val gz = (sin(t * PI * 3) * 1.5 + random.nextFloat(-0.2, 0.2)).toFloat()

            // 加速度：向上动作 az 为负
            val peak = exp(-((t - 0.5) * 8).pow(2)) * 20.0
            val ax = (peak * 0.2 + random.nextFloat(-1, 1)).toFloat()
            val ay = (peak * 0.3 + random.nextFloat(-1, 1)).toFloat()
            val az = (-peak * 0.5 - 9.81 + random.nextFloat(-1, 1)).toFloat()

            val mx = (20.0 + random.nextFloat(-1, 1)).toFloat()
            val my = (30.0 + random.nextFloat(-1, 1)).toFloat()
            val mz = (40.0 + random.nextFloat(-1, 1)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = mx, my = my, mz = mz
                )
            )
        }

        return dataPoints
    }

    /**
     * 生成正手截击模拟数据
     * 特征：时长短促，小幅度
     */
    fun generateForehandVolley(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()
        val duration = 20

        for (i in 0 until duration) {
            val t = i / 100.0
            val timestamp = startTime + i * 10

            // 截击特征：短促，小幅度顺时针
            val gx = (sin(t * PI * 10) * 0.2 + random.nextFloat(-0.1, 0.1)).toFloat()
            val gy = (exp(-((t - 0.1) * 20).pow(2)) * 3.0 + random.nextFloat(-0.1, 0.1)).toFloat()
            val gz = (cos(t * PI * 5) * 0.3 + random.nextFloat(-0.1, 0.1)).toFloat()

            // 加速度：短促脉冲
            val peak = exp(-((t - 0.1) * 25).pow(2)) * 8.0
            val ax = (peak * 0.3 + 9.81 * 0.05 + random.nextFloat(-0.3, 0.3)).toFloat()
            val ay = (peak * 0.4 + random.nextFloat(-0.3, 0.3)).toFloat()
            val az = (peak * 0.2 - 9.81 + random.nextFloat(-0.3, 0.3)).toFloat()

            val mx = (20.0 + random.nextFloat(-1, 1)).toFloat()
            val my = (30.0 + random.nextFloat(-1, 1)).toFloat()
            val mz = (40.0 + random.nextFloat(-1, 1)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = mx, my = my, mz = mz
                )
            )
        }

        return dataPoints
    }

    /**
     * 生成反手截击模拟数据
     * 特征：时长短促，小幅度逆时针
     */
    fun generateBackhandVolley(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()
        val duration = 20

        for (i in 0 until duration) {
            val t = i / 100.0
            val timestamp = startTime + i * 10

            // 反手截击：短促，逆时针
            val gx = (sin(t * PI * 10) * 0.2 + random.nextFloat(-0.1, 0.1)).toFloat()
            val gy = (-exp(-((t - 0.1) * 20).pow(2)) * 3.0 + random.nextFloat(-0.1, 0.1)).toFloat()
            val gz = (-cos(t * PI * 5) * 0.3 + random.nextFloat(-0.1, 0.1)).toFloat()

            // 加速度
            val peak = exp(-((t - 0.1) * 25).pow(2)) * 7.0
            val ax = (peak * 0.3 + 9.81 * 0.05 + random.nextFloat(-0.3, 0.3)).toFloat()
            val ay = (peak * 0.3 + random.nextFloat(-0.3, 0.3)).toFloat()
            val az = (peak * 0.2 - 9.81 + random.nextFloat(-0.3, 0.3)).toFloat()

            val mx = (20.0 + random.nextFloat(-1, 1)).toFloat()
            val my = (30.0 + random.nextFloat(-1, 1)).toFloat()
            val mz = (40.0 + random.nextFloat(-1, 1)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = mx, my = my, mz = mz
                )
            )
        }

        return dataPoints
    }

    /**
     * 生成静止状态数据（无动作）
     */
    fun generateIdle(duration: Int = 100): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()

        for (i in 0 until duration) {
            val timestamp = startTime + i * 10

            // 静止：只有重力和少量噪声
            val ax = (9.81 * 0.1 + random.nextFloat(-0.2, 0.2)).toFloat()
            val ay = (random.nextFloat(-0.2, 0.2)).toFloat()
            val az = (-9.81 + random.nextFloat(-0.2, 0.2)).toFloat()

            val gx = (random.nextFloat(-0.05, 0.05)).toFloat()
            val gy = (random.nextFloat(-0.05, 0.05)).toFloat()
            val gz = (random.nextFloat(-0.05, 0.05)).toFloat()

            val mx = (20.0 + random.nextFloat(-0.5, 0.5)).toFloat()
            val my = (30.0 + random.nextFloat(-0.5, 0.5)).toFloat()
            val mz = (40.0 + random.nextFloat(-0.5, 0.5)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = mx, my = my, mz = mz
                )
            )
        }

        return dataPoints
    }

    /**
     * 生成完整训练会话数据
     * 包含多种击球类型和静止间隔
     */
    fun generateTrainingSession(): List<SensorDataPoint> {
        val allData = mutableListOf<SensorDataPoint>()

        // 添加静止间隔
        allData.addAll(generateIdle(50))

        // 随机添加各种击球
        val swings = listOf(
            generateForehand(),
            generateBackhand(),
            generateForehand(),
            generateSlice(),
            generateServe(),
            generateForehandVolley(),
            generateBackhandVolley(),
            generateBackhand(),
            generateForehand()
        )

        for ((index, swing) in swings.withIndex()) {
            allData.addAll(swing)
            if (index < swings.size - 1) {
                allData.addAll(generateIdle(random.nextInt(30, 60)))
            }
        }

        allData.addAll(generateIdle(50))

        return allData
    }
}

/**
 * Random 扩展函数：生成指定范围的随机 Float
 */
fun Random.nextFloat(from: Float, to: Float): Float {
    return from + (to - from) * nextFloat()
}
