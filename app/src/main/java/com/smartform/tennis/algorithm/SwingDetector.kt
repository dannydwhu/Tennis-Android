package com.smartform.tennis.algorithm

import com.smartform.tennis.algorithm.model.*
import kotlin.math.sqrt

/**
 * 动作检测器
 *
 * 基于状态机从连续数据流中切割出单个击球动作
 *
 * 状态转换：
 * IDLE(静止) → PRE_TRIGGER(预触发) → TRIGGERED(已触发) → RECOVERY(恢复) → IDLE
 */
class SwingDetector {

    // 当前状态
    private var currentState = DetectionState.IDLE

    // 数据缓冲区
    private val preTriggerBuffer = mutableListOf<SensorDataPoint>()  // 预触发缓冲
    private val actionWindow = mutableListOf<SensorDataPoint>()      // 动作窗口

    // 计时器
    private var lastTriggerTime = 0L
    private var recoveryStartTime = 0L

    // 阈值参数（可调优）
    private val triggerThreshold = 12.0f       // 触发阈值 (m/s²) - 约 1.2g
    private val staticMinThreshold = 8.0f      // 静止下限 (m/s²) - 约 0.8g
    private val staticMaxThreshold = 11.5f     // 静止上限 (m/s²) - 约 1.17g
    private val angularVelocityThreshold = 2.0f // 角速度阈值 (rad/s)

    // 时间参数 (ms)
    private val preTriggerDuration = 100L   // 预触发缓冲时长 (向前 0.1s)
    private val postTriggerDuration = 300L  // 动作结束后追加时长 (向后 0.3s)
    private val recoveryDuration = 200L     // 恢复期判定时长

    // 采样率 (100Hz)
    private val sampleInterval = 10L  // 10ms 一个数据点

    /**
     * 处理单个数据点
     *
     * @param dataPoint 预处理后的传感器数据
     * @return 如果检测到完整动作，返回 DetectionResult(isSwingDetected=true)
     */
    fun processDataPoint(dataPoint: SensorDataPoint): DetectionResult {
        val accelMag = dataPoint.accelerationMagnitude
        val angularMag = dataPoint.angularVelocityMagnitude

        return when (currentState) {
            DetectionState.IDLE -> handleIdleState(dataPoint, accelMag, angularMag)
            DetectionState.PRE_TRIGGER -> handlePreTriggerState(dataPoint, accelMag, angularMag)
            DetectionState.TRIGGERED -> handleTriggeredState(dataPoint, accelMag)
            DetectionState.RECOVERY -> handleRecoveryState(dataPoint, accelMag)
        }
    }

    /**
     * 处理静止状态
     */
    private fun handleIdleState(
        dataPoint: SensorDataPoint,
        accelMag: Float,
        angularMag: Float
    ): DetectionResult {
        // 检查是否开始有动作迹象
        if (accelMag > triggerThreshold || angularMag > angularVelocityThreshold) {
            // 进入预触发状态
            currentState = DetectionState.PRE_TRIGGER
            preTriggerBuffer.clear()
            preTriggerBuffer.add(dataPoint)
            lastTriggerTime = dataPoint.timestamp

            // 估算需要的缓冲点数
            val preTriggerPoints = (preTriggerDuration / sampleInterval).toInt()

            // 如果已经有足够数据，直接进入触发状态
            if (preTriggerBuffer.size >= preTriggerPoints) {
                currentState = DetectionState.TRIGGERED
                actionWindow.clear()
                actionWindow.addAll(preTriggerBuffer)
                return DetectionResult(false, null, null, null)
            }
        }
        return DetectionResult(false, null, null, null)
    }

    /**
     * 处理预触发缓冲状态
     */
    private fun handlePreTriggerState(
        dataPoint: SensorDataPoint,
        accelMag: Float,
        angularMag: Float
    ): DetectionResult {
        preTriggerBuffer.add(dataPoint)

        // 检查是否达到触发阈值
        if (accelMag > triggerThreshold || angularMag > angularVelocityThreshold) {
            // 检查缓冲是否足够
            val preTriggerPoints = (preTriggerDuration / sampleInterval).toInt()

            if (preTriggerBuffer.size >= preTriggerPoints) {
                // 进入触发状态
                currentState = DetectionState.TRIGGERED
                actionWindow.clear()
                actionWindow.addAll(preTriggerBuffer)  // 包含预触发缓冲
                return DetectionResult(false, null, null, null)
            } else {
                // 缓冲不足，直接进入触发状态（少采集一些前置数据）
                currentState = DetectionState.TRIGGERED
                actionWindow.clear()
                actionWindow.addAll(preTriggerBuffer)
                return DetectionResult(false, null, null, null)
            }
        }

        // 如果信号回落，返回 IDLE
        if (accelMag < staticMinThreshold && angularMag < angularVelocityThreshold) {
            currentState = DetectionState.IDLE
            preTriggerBuffer.clear()
        }

        return DetectionResult(false, null, null, null)
    }

    /**
     * 处理动作触发状态
     */
    private fun handleTriggeredState(
        dataPoint: SensorDataPoint,
        accelMag: Float
    ): DetectionResult {
        actionWindow.add(dataPoint)

        // 检查是否进入恢复期
        if (accelMag < staticMinThreshold) {
            currentState = DetectionState.RECOVERY
            recoveryStartTime = dataPoint.timestamp
        }

        return DetectionResult(false, null, null, null)
    }

    /**
     * 处理恢复期状态
     */
    private fun handleRecoveryState(
        dataPoint: SensorDataPoint,
        accelMag: Float
    ): DetectionResult {
        // 恢复期内继续收集数据
        val timeInRecovery = dataPoint.timestamp - recoveryStartTime

        if (timeInRecovery < postTriggerDuration) {
            actionWindow.add(dataPoint)

            // 检查是否保持静止
            if (timeInRecovery >= recoveryDuration &&
                accelMag in staticMinThreshold..staticMaxThreshold
            ) {
                // 保持静止，动作结束
                return finalizeSwing(dataPoint)
            }
        } else {
            // 恢复期超时，强制结束
            return finalizeSwing(dataPoint)
        }

        return DetectionResult(false, null, null, null)
    }

    /**
     * 完成动作检测，输出结果
     */
    private fun finalizeSwing(lastDataPoint: SensorDataPoint): DetectionResult {
        currentState = DetectionState.IDLE

        val result = DetectionResult(
            isSwingDetected = true,
            dataWindow = actionWindow.toList(),
            startTime = actionWindow.firstOrNull()?.timestamp,
            endTime = lastDataPoint.timestamp
        )

        // 重置缓冲
        actionWindow.clear()
        preTriggerBuffer.clear()

        return result
    }

    /**
     * 批量处理数据
     *
     * @return 检测到的所有动作窗口
     */
    fun processBatch(dataPoints: List<SensorDataPoint>): List<List<SensorDataPoint>> {
        val swingWindows = mutableListOf<List<SensorDataPoint>>()

        for (dataPoint in dataPoints) {
            val result = processDataPoint(dataPoint)
            if (result.isSwingDetected && result.dataWindow != null) {
                swingWindows.add(result.dataWindow)
            }
        }

        return swingWindows
    }

    /**
     * 重置检测器状态
     */
    fun reset() {
        currentState = DetectionState.IDLE
        preTriggerBuffer.clear()
        actionWindow.clear()
        lastTriggerTime = 0L
        recoveryStartTime = 0L
    }

    /**
     * 获取当前状态
     */
    fun getCurrentState(): DetectionState {
        return currentState
    }
}
