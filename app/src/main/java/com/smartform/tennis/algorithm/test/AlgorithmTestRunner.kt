package com.smartform.tennis.algorithm.test

import com.smartform.tennis.algorithm.model.SwingType

/**
 * 算法测试运行器
 *
 * 运行所有算法测试并生成报告
 */
fun main() {
    println("\n")
    println("╔════════════════════════════════════════════╗")
    println("║   Smartform Tennis 算法测试套件             ║")
    println("║   Swing Recognition Algorithm Test Suite   ║")
    println("╚════════════════════════════════════════════╝")

    val tester = SwingAlgorithmTester()
    val debugger = AlgorithmDebugger()

    // ========== 第一部分是单测试 ==========
    println("\n【第一部分】单动作识别测试")
    println("─────────────────────────────────────────")

    val testResults = mutableListOf<TestResult>()

    // 测试正手
    println("\n▶ 测试正手击球...")
    testResults.add(tester.testSingleSwingType(SwingType.FOREHAND) {
        MockSensorDataGenerator().generateForehand()
    })

    // 测试反手
    println("\n▶ 测试反手击球...")
    testResults.add(tester.testSingleSwingType(SwingType.BACKHAND) {
        MockSensorDataGenerator().generateBackhand()
    })

    // 测试切削
    println("\n▶ 测试切削...")
    testResults.add(tester.testSingleSwingType(SwingType.SLICE) {
        MockSensorDataGenerator().generateSlice()
    })

    // 测试发球
    println("\n▶ 测试发球/高压...")
    testResults.add(tester.testSingleSwingType(SwingType.SERVE) {
        MockSensorDataGenerator().generateServe()
    })

    // 测试正手截击
    println("\n▶ 测试正手截击...")
    testResults.add(tester.testSingleSwingType(SwingType.FOREHAND_VOLLEY) {
        MockSensorDataGenerator().generateForehandVolley()
    })

    // 测试反手截击
    println("\n▶ 测试反手截击...")
    testResults.add(tester.testSingleSwingType(SwingType.BACKHAND_VOLLEY) {
        MockSensorDataGenerator().generateBackhandVolley()
    })

    // ========== 第二部分是完整会话测试 ==========
    println("\n\n【第二部分】完整训练会话测试")
    println("─────────────────────────────────────────")
    val sessionResult = tester.testTrainingSession()

    // ========== 第三部分是生成报告 ==========
    println("\n\n【第三部分】测试报告")
    println("─────────────────────────────────────────")
    println(debugger.generateTestReport(testResults))

    // ========== 总体评估 ==========
    println("\n【总体评估】")
    val overallAccuracy = testResults.sumOf { it.correctCount }.toFloat() /
                          testResults.sumOf { it.totalDetected }

    when {
        overallAccuracy >= 0.9 -> println("  ✓ 优秀！算法识别准确率达到 ${String.format("%.1f%%", overallAccuracy * 100)}")
        overallAccuracy >= 0.8 -> println("  ✓ 良好！算法识别准确率为 ${String.format("%.1f%%", overallAccuracy * 100)}")
        overallAccuracy >= 0.7 -> println("  ⚠ 合格！算法识别准确率为 ${String.format("%.1f%%", overallAccuracy * 100)}，但有提升空间")
        else -> println("  ✗ 需要优化！算法识别准确率仅为 ${String.format("%.1f%%", overallAccuracy * 100)}")
    }

    // ========== 调优建议 ==========
    println("\n【调优建议】")
    testResults.filter { it.accuracy < 0.8 }.forEach { result ->
        println("\n  ${result.swingType.displayName} (准确率 ${String.format("%.1f%%", result.accuracy * 100)}):")

        // 分析错误识别的主要类型
        val mainError = result.distribution
            .filterKeys { it != result.swingType }
            .maxByOrNull { it.value }

        mainError?.let { (wrongType, count) ->
            println("    主要错误：被识别为 ${wrongType.displayName} ($count 次)")
            println("    建议调整:")

            when {
                result.swingType == SwingType.SLICE -> {
                    println("      - 降低 kurtosisThreshold 阈值，使波形更平缓才被识别为切削")
                }
                result.swingType == SwingType.FOREHAND || result.swingType == SwingType.BACKHAND -> {
                    println("      - 调整 rotationAngleThreshold 使旋转方向判断更严格")
                }
                result.swingType == SwingType.FOREHAND_VOLLEY || result.swingType == SwingType.BACKHAND_VOLLEY -> {
                    println("      - 降低 volleyDurationThreshold，使截击时长判断更严格")
                }
            }
        }
    }

    println("\n")
    println("测试完成！")
}
