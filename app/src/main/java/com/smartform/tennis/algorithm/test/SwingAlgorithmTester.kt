package com.smartform.tennis.algorithm.test

import com.smartform.tennis.algorithm.*
import com.smartform.tennis.algorithm.model.SwingType

/**
 * 算法测试器
 *
 * 使用模拟数据测试击球识别算法的准确率
 */
class SwingAlgorithmTester {

    private val mockGenerator = MockSensorDataGenerator()
    private val swingEngine = TennisSwingEngine()
    private val detector = SwingDetector()
    private val preprocessor = DataPreprocessor()
    private val classifier = SwingClassifier()
    private val featureExtractor = FeatureExtractor()

    /**
     * 测试单个击球类型的识别
     */
    fun testSingleSwingType(
        swingType: SwingType,
        dataGenerator: () -> List<com.smartform.tennis.algorithm.model.SensorDataPoint>
    ): TestResult {
        println("\n=== 测试：${swingType.displayName} ===")

        val testData = dataGenerator()
        val processedData = testData.map { preprocessor.process(it) }

        var detectedCount = 0
        var correctCount = 0
        val detectedTypes = mutableMapOf<SwingType, Int>()

        // 使用检测器切割动作
        val windows = detector.processBatch(processedData)

        for ((index, window) in windows.withIndex()) {
            val features = featureExtractor.extractFeatures(window)
            val result = classifier.classify(features)

            detectedCount++
            detectedTypes[result.swingType] = (detectedTypes[result.swingType] ?: 0) + 1

            if (result.swingType == swingType) {
                correctCount++
            }

            println("  动作 ${index + 1}: 识别为 ${result.swingType.displayName} " +
                    "(置信度：${String.format("%.2f", result.confidence)})")

            if (result.swingType != swingType) {
                println("    ⚠️ 识别错误！期望：${swingType.displayName}")
                println("    特征：时长=${features.durationMs}ms, " +
                        "旋转=${features.rotationDirection}, " +
                        "峰度=${String.format("%.2f", features.kurtosis)}")
            }
        }

        val accuracy = if (detectedCount > 0) correctCount.toFloat() / detectedCount else 0f

        println("\n结果：正确 $correctCount/$detectedCount, 准确率：${String.format("%.1f%%", accuracy * 100)}")
        println("识别分布：${detectedTypes.map { "${it.key.displayName}=${it.value}" }.joinToString(", ")}")

        return TestResult(
            swingType = swingType,
            totalDetected = detectedCount,
            correctCount = correctCount,
            accuracy = accuracy,
            distribution = detectedTypes
        )
    }

    /**
     * 测试完整训练会话
     */
    fun testTrainingSession(): SessionTestResult {
        println("\n========== 完整训练会话测试 ==========")

        // 重置引擎
        swingEngine.reset()
        swingEngine.startSession(1, 1)

        val sessionData = mockGenerator.generateTrainingSession()
        val expectedShots = mapOf(
            SwingType.FOREHAND to 3,
            SwingType.BACKHAND to 2,
            SwingType.SLICE to 1,
            SwingType.SERVE to 1,
            SwingType.FOREHAND_VOLLEY to 1,
            SwingType.BACKHAND_VOLLEY to 1
        )

        val detectedEvents = mutableListOf<com.smartform.tennis.algorithm.model.SwingEvent>()

        for (dataPoint in sessionData) {
            val event = swingEngine.processDataPoint(dataPoint)
            event?.let {
                detectedEvents.add(it)
                println("检测到击球：${it.swingType.displayName}, 速度：${String.format("%.1f", it.maxSpeed)} km/h")
            }
        }

        val finalStats = swingEngine.endSession()

        println("\n=== 会话统计 ===")
        println("总击球数：${finalStats.totalSwings}")
        println("各类型计数:")
        for ((type, count) in finalStats.swingCounts) {
            println("  ${type.displayName}: $count")
        }

        println("\n最大速度:")
        for ((type, speed) in finalStats.maxSpeeds) {
            println("  ${type.displayName}: ${String.format("%.1f", speed)} km/h")
        }

        return SessionTestResult(
            expectedShots = expectedShots,
            detectedEvents = detectedEvents,
            finalStats = finalStats
        )
    }

    /**
     * 批量测试所有击球类型
     */
    fun runAllTests(): OverallTestResult {
        println("========================================")
        println("    Smartform Tennis 算法测试套件")
        println("========================================")

        val results = mutableListOf<TestResult>()

        // 测试每种击球类型
        results.add(testSingleSwingType(SwingType.FOREHAND) { mockGenerator.generateForehand() })
        results.add(testSingleSwingType(SwingType.BACKHAND) { mockGenerator.generateBackhand() })
        results.add(testSingleSwingType(SwingType.SLICE) { mockGenerator.generateSlice() })
        results.add(testSingleSwingType(SwingType.SERVE) { mockGenerator.generateServe() })
        results.add(testSingleSwingType(SwingType.FOREHAND_VOLLEY) { mockGenerator.generateForehandVolley() })
        results.add(testSingleSwingType(SwingType.BACKHAND_VOLLEY) { mockGenerator.generateBackhandVolley() })

        // 计算总体准确率
        val totalDetected = results.sumOf { it.totalDetected }
        val totalCorrect = results.sumOf { it.correctCount }
        val overallAccuracy = if (totalDetected > 0) totalCorrect.toFloat() / totalDetected else 0f

        println("\n========================================")
        println("    总体测试结果")
        println("========================================")
        println("总检测次数：$totalDetected")
        println("正确识别次数：$totalCorrect")
        println("总体准确率：${String.format("%.1f%%", overallAccuracy * 100)}")

        // 分析每种类型的准确率
        println("\n各类型准确率:")
        results.sortedByDescending { it.accuracy }.forEach {
            val status = if (it.accuracy >= 0.8) "✓" else if (it.accuracy >= 0.6) "⚠" else "✗"
            println("  $status ${it.swingType.displayName}: ${String.format("%.1f%%", it.accuracy * 100)}")
        }

        return OverallTestResult(
            results = results,
            overallAccuracy = overallAccuracy,
            totalDetected = totalDetected,
            totalCorrect = totalCorrect
        )
    }
}

/**
 * 单次测试结果
 */
data class TestResult(
    val swingType: SwingType,
    val totalDetected: Int,
    val correctCount: Int,
    val accuracy: Float,
    val distribution: Map<SwingType, Int>
)

/**
 * 会话测试结果
 */
data class SessionTestResult(
    val expectedShots: Map<SwingType, Int>,
    val detectedEvents: List<com.smartform.tennis.algorithm.model.SwingEvent>,
    val finalStats: com.smartform.tennis.algorithm.EngineStats
)

/**
 * 总体测试结果
 */
data class OverallTestResult(
    val results: List<TestResult>,
    val overallAccuracy: Float,
    val totalDetected: Int,
    val totalCorrect: Int
)
