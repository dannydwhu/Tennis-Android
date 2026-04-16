package com.smartform.tennis.algorithm

import com.smartform.tennis.algorithm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 网球动作识别引擎
 *
 * 统一入口，整合所有算法模块
 *
 * 使用流程：
 * 1. 创建引擎实例
 * 2. 调用 startSession 开始训练会话
 * 3. 持续调用 processDataPoint 处理传感器数据
 * 4. 通过 swingEventFlow 接收识别结果
 * 5. 调用 endSession 结束会话
 */
class TennisSwingEngine {

    // 算法模块
    private val preprocessor = DataPreprocessor()
    private val detector = SwingDetector()
    private val classifier = SwingClassifier()
    private val speedCalculator = SpeedCalculator()

    // 会话状态
    private var currentSessionId: Long = -1
    private var currentUserId: Long = -1
    private var isSessionActive = false

    // 统计数据
    private val swingCountMap = mutableMapOf<SwingType, Int>()
    private val maxSpeedMap = mutableMapOf<SwingType, Float>()
    private var totalSwingCount = 0

    // 识别结果流
    private val _swingEventFlow = MutableStateFlow<SwingEvent?>(null)
    val swingEventFlow: StateFlow<SwingEvent?> = _swingEventFlow.asStateFlow()

    // 统计数据流
    private val _statsFlow = MutableStateFlow(EngineStats.empty())
    val statsFlow: StateFlow<EngineStats> = _statsFlow.asStateFlow()

    /**
     * 开始训练会话
     */
    fun startSession(sessionId: Long, userId: Long) {
        currentSessionId = sessionId
        currentUserId = userId
        isSessionActive = true

        // 重置所有模块
        preprocessor.reset()
        detector.reset()
        swingCountMap.clear()
        maxSpeedMap.clear()
        totalSwingCount = 0

        _statsFlow.value = EngineStats.empty()
    }

    /**
     * 处理单个传感器数据点
     *
     * @param dataPoint 原始传感器数据
     * @return 如果检测到击球，返回 SwingEvent
     */
    fun processDataPoint(dataPoint: SensorDataPoint): SwingEvent? {
        if (!isSessionActive) return null

        // 1. 数据预处理（去重力、平滑）
        val processedData = preprocessor.process(dataPoint)

        // 2. 动作检测
        val detectionResult = detector.processDataPoint(processedData)

        // 3. 如果检测到完整动作，进行分类和速度计算
        if (detectionResult.isSwingDetected && detectionResult.dataWindow != null) {
            val swingEvent = classifyAndCalculate(detectionResult.dataWindow)

            if (swingEvent?.swingType != SwingType.UNKNOWN) {
                // 更新统计
                updateStats(swingEvent)

                // 发送事件
                _swingEventFlow.value = swingEvent
                _statsFlow.value = getCurrentStats()

                return swingEvent
            }
        }

        return null
    }

    /**
     * 批量处理数据点
     */
    fun processBatch(dataPoints: List<SensorDataPoint>): List<SwingEvent> {
        val events = mutableListOf<SwingEvent>()

        for (dataPoint in dataPoints) {
            val event = processDataPoint(dataPoint)
            if (event != null && event.swingType != SwingType.UNKNOWN) {
                events.add(event)
            }
        }

        return events
    }

    /**
     * 分类和速度计算
     */
    private fun classifyAndCalculate(dataWindow: List<SensorDataPoint>): SwingEvent? {
        // 使用分类器的完整处理流程
        return classifier.processWindow(dataWindow, currentSessionId, currentUserId)
    }

    /**
     * 更新统计数据
     */
    private fun updateStats(swingEvent: SwingEvent) {
        val type = swingEvent.swingType

        // 更新计数
        swingCountMap[type] = (swingCountMap[type] ?: 0) + 1
        totalSwingCount++

        // 更新最大速度
        val currentMax = maxSpeedMap[type] ?: 0f
        if (swingEvent.maxSpeed > currentMax) {
            maxSpeedMap[type] = swingEvent.maxSpeed
        }
    }

    /**
     * 获取当前统计
     */
    fun getCurrentStats(): EngineStats {
        return EngineStats(
            totalSwings = totalSwingCount,
            swingCounts = swingCountMap.toMap(),
            maxSpeeds = maxSpeedMap.toMap(),
            sessionActive = isSessionActive
        )
    }

    /**
     * 结束训练会话
     *
     * @return 最终统计
     */
    fun endSession(): EngineStats {
        isSessionActive = false
        val finalStats = getCurrentStats()

        // 重置
        currentSessionId = -1
        currentUserId = -1

        return finalStats
    }

    /**
     * 重置引擎
     */
    fun reset() {
        preprocessor.reset()
        detector.reset()
        swingCountMap.clear()
        maxSpeedMap.clear()
        totalSwingCount = 0
        isSessionActive = false
        _statsFlow.value = EngineStats.empty()
        _swingEventFlow.value = null
    }
}

/**
 * 引擎统计数据
 */
data class EngineStats(
    val totalSwings: Int,
    val swingCounts: Map<SwingType, Int>,
    val maxSpeeds: Map<SwingType, Float>,
    val sessionActive: Boolean
) {
    // 获取特定击球类型的计数
    fun getCount(type: SwingType): Int = swingCounts[type] ?: 0

    // 获取特定击球类型的最大速度
    fun getMaxSpeed(type: SwingType): Float = maxSpeeds[type] ?: 0f

    companion object {
        fun empty() = EngineStats(
            totalSwings = 0,
            swingCounts = emptyMap(),
            maxSpeeds = emptyMap(),
            sessionActive = false
        )
    }
}
