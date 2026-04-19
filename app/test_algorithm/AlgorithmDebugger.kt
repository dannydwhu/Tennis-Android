package com.smartform.tennis.algorithm.test

import com.smartform.tennis.algorithm.model.SensorDataPoint
import com.smartform.tennis.algorithm.model.SwingType
import kotlin.math.sqrt

/**
 * 算法调试工具
 *
 * 可视化和分析算法中间结果
 */
class AlgorithmDebugger {

    /**
     * 打印数据点统计信息
     */
    fun printDataStats(dataPoints: List<SensorDataPoint>, label: String = "数据") {
        println("\n=== $label 统计 ===")
        println("数据点数量：${dataPoints.size}")
        println("时长：${(dataPoints.last().timestamp - dataPoints.first().timestamp) / 1000.0f} 秒")

        val accelMagnitudes = dataPoints.map {
            sqrt(it.ax * it.ax + it.ay * it.ay + it.az * it.az)
        }
        val gyroMagnitudes = dataPoints.map {
            sqrt(it.gx * it.gx + it.gy * it.gy + it.gz * it.gz)
        }

        println("\n加速度统计:")
        println("  最大值：${String.format("%.2f", accelMagnitudes.maxOrNull() ?: 0)} m/s²")
        println("  最小值：${String.format("%.2f", accelMagnitudes.minOrNull() ?: 0)} m/s²")
        println("  平均值：${String.format("%.2f", accelMagnitudes.average())} m/s²")

        println("\n角速度统计:")
        println("  最大值：${String.format("%.2f", gyroMagnitudes.maxOrNull() ?: 0)} rad/s")
        println("  最小值：${String.format("%.2f", gyroMagnitudes.minOrNull() ?: 0)} rad/s")
        println("  平均值：${String.format("%.2f", gyroMagnitudes.average())} rad/s")
    }

    /**
     * 打印动作窗口特征
     */
    fun printWindowFeatures(window: List<SensorDataPoint>) {
        println("\n=== 动作窗口特征 ===")
        println("时长：${window.last().timestamp - window.first().timestamp} ms")
        println("数据点数：${window.size}")

        val accelMag = window.map {
            sqrt(it.ax * it.ax + it.ay * it.ay + it.az * it.az)
        }

        println("\n加速度序列（前 10 个）:")
        accelMag.take(10).forEachIndexed { index, mag ->
            print("${String.format("%.2f", mag)} ")
            if ((index + 1) % 5 == 0) println()
        }
        println()
    }

    /**
     * ASCII 图表显示加速度波形
     */
    fun printAccelerationChart(dataPoints: List<SensorDataPoint>, width: Int = 60, height: Int = 15) {
        val accelMag = dataPoints.map {
            sqrt(it.ax * it.ax + it.ay * it.ay + it.az * it.az)
        }

        val min = accelMag.minOrNull() ?: 0f
        val max = accelMag.maxOrNull() ?: 20f
        val range = max - min

        println("\n=== 加速度波形图 (合加速度) ===")
        println("Y 轴：${String.format("%.1f", max)} - ${String.format("%.1f", min)} m/s²")
        println("X 轴：时间 (${dataPoints.size} 个数据点)")
        println()

        // 降采样以适应终端宽度
        val step = maxOf(1, dataPoints.size / width)
        val sampled = accelMag.filterIndexed { index, _ -> index % step == 0 }.take(width)

        for (row in height downTo 1) {
            val threshold = min + (row.toFloat() / height) * range
            print(String.format("%5.1f │", threshold))

            for (value in sampled) {
                if (value >= threshold) {
                    print("█")
                } else {
                    print(" ")
                }
            }
            println()
        }

        println("      └" + "─".repeat(width))
    }

    /**
     * 打印分类器决策过程
     */
    fun printClassificationProcess(
        features: com.smartform.tennis.algorithm.model.SwingFeatures,
        result: com.smartform.tennis.algorithm.ClassificationResult
    ) {
        println("\n=== 分类决策过程 ===")
        println("输入特征:")
        println("  时长：${features.durationMs} ms")
        println("  旋转方向：${features.rotationDirection}")
        println("  旋转角度：${String.format("%.2f", features.rotationAngle)} rad")
        println("  峰度：${String.format("%.2f", features.kurtosis)}")
        println("  最大角速度：${String.format("%.2f", features.maxAngularVelocity)} rad/s")
        println("  平均 az：${String.format("%.2f", features.avgAz)} m/s²")

        println("\n分类结果:")
        println("  类型：${result.swingType.displayName}")
        println("  置信度：${String.format("%.2f", result.confidence)}")

        if (result.suggestion != null) {
            println("  AI 建议：${result.suggestion}")
        }

        // 显示决策路径
        println("\n决策路径:")
        when {
            features.durationMs < 300 -> println("  → 时长 < 300ms → 截击类")
            features.durationMs > 600 -> println("  → 时长 > 600ms → 发球/高压类")
            else -> println("  → 时长 300-600ms → 常规击球类")
        }

        when (features.rotationDirection) {
            com.smartform.tennis.algorithm.RotationDirection.CLOCKWISE ->
                println("  → 顺时针旋转 → 正手")
            com.smartform.tennis.algorithm.RotationDirection.COUNTER_CLOCKWISE ->
                println("  → 逆时针旋转 → 反手")
            else -> println("  → 旋转方向不明显")
        }

        if (features.kurtosis < 0.5f) {
            println("  → 峰度 < 0.5 → 波形平缓 → 切削特征")
        } else {
            println("  → 峰度 >= 0.5 → 波形尖锐 → 击球特征")
        }
    }

    /**
     * 对比期望和实际结果
     */
    fun printComparison(expected: SwingType, actual: SwingType, confidence: Float) {
        val status = if (expected == actual) "✓ 正确" else "✗ 错误"
        val color = if (expected == actual) "" else " [需要调优]"

        println("$status | 期望：${expected.displayName}, 实际：${actual.displayName} " +
                "(置信度：${String.format("%.2f", confidence)})$color")
    }

    /**
     * 生成测试报告
     */
    fun generateTestReport(testResults: List<TestResult>): String {
        val report = buildString {
            appendLine("========================================")
            appendLine("    Smartform Tennis 算法测试报告")
            appendLine("========================================")
            appendLine()
            appendLine("测试日期：${java.time.LocalDateTime.now()}")
            appendLine()

            appendLine("各类型识别准确率:")
            testResults.sortedByDescending { it.accuracy }.forEach { result ->
                val bar = "█".repeat((result.accuracy * 10).toInt()) +
                          "░".repeat(10 - (result.accuracy * 10).toInt())
                appendLine("  ${result.swingType.displayName}: [$bar] ${String.format("%.1f%%", result.accuracy * 100)}")
            }

            appendLine()
            val totalCorrect = testResults.sumOf { it.correctCount }
            val totalDetected = testResults.sumOf { it.totalDetected }
            val overallAccuracy = if (totalDetected > 0) totalCorrect.toFloat() / totalDetected else 0f

            appendLine("总体表现:")
            appendLine("  总检测次数：$totalDetected")
            appendLine("  正确识别：$totalCorrect")
            appendLine("  总体准确率：${String.format("%.1f%%", overallAccuracy * 100)}")

            appendLine()
            appendLine("需要优化的类型:")
            testResults.filter { it.accuracy < 0.8 }.forEach { result ->
                appendLine("  - ${result.swingType.displayName}: ${String.format("%.1f%%", result.accuracy * 100)}")
            }
        }

        return report
    }
}
