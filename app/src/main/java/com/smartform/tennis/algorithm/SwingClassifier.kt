package com.smartform.tennis.algorithm

import com.smartform.tennis.algorithm.model.SensorDataPoint
import com.smartform.tennis.algorithm.model.SwingEvent
import com.smartform.tennis.algorithm.model.SwingType

/**
 * 动作分类器
 *
 * 基于规则引擎和决策树识别 6 种击球类型
 *
 * 决策树逻辑：
 * 1. 首先根据动作时长分类（截击 vs 常规 vs 发球）
 * 2. 根据旋转方向判断正手/反手
 * 3. 根据波形特征区分击球/切削
 * 4. 根据高度特征区分发球/高压
 */
class SwingClassifier {

    // 分类阈值（可调优）
    private val volleyDurationThreshold = 300L          // 截击时长上限 (ms)
    private val serveDurationThreshold = 600L           // 发球时长下限 (ms)
    private val kurtosisThreshold = 0.5f                // 峰度阈值（区分击球/切削）
    private val rotationAngleThreshold = 1.0f           // 旋转角度阈值 (rad)
    private val highServeAzThreshold = -5.0f            // 发球上手高度阈值
    private val confidenceHigh = 0.8f                   // 高置信度阈值
    private val confidenceMedium = 0.6f                 // 中置信度阈值

    /**
     * 分类单个击球动作
     *
     * @param features 提取的动作特征
     * @return SwingType 和置信度
     */
    fun classify(features: SwingFeatures): ClassificationResult {
        // 特征验证
        if (features.durationMs < 50) {
            return ClassificationResult(SwingType.UNKNOWN, 0.0f, "动作时长过短")
        }

        // 决策树分类
        val swingType = decideByDecisionTree(features)
        val confidence = calculateConfidence(features, swingType)
        val suggestion = generateSuggestion(features, swingType)

        return ClassificationResult(swingType, confidence, suggestion)
    }

    /**
     * 决策树分类主逻辑
     */
    private fun decideByDecisionTree(features: SwingFeatures): SwingType {
        // 第一层：按动作时长分类
        return when {
            // 截击类（短促）
            features.durationMs < volleyDurationThreshold -> {
                classifyVolley(features)
            }

            // 发球/高压类（时长较长）
            features.durationMs > serveDurationThreshold -> {
                classifyServeOrSmash(features)
            }

            // 常规击球
            else -> {
                classifyNormalShot(features)
            }
        }
    }

    /**
     * 分类截击球
     */
    private fun classifyVolley(features: SwingFeatures): SwingType {
        return when (features.rotationDirection) {
            RotationDirection.CLOCKWISE -> SwingType.FOREHAND_VOLLEY
            RotationDirection.COUNTER_CLOCKWISE -> SwingType.BACKHAND_VOLLEY
            RotationDirection.UNKNOWN -> {
                // 如果旋转方向不明显，根据加速度模式判断
                if (features.maxAcceleration > 15.0f) {
                    SwingType.FOREHAND_VOLLEY  // 默认正手
                } else {
                    SwingType.UNKNOWN
                }
            }
        }
    }

    /**
     * 分类发球/高压
     */
    private fun classifyServeOrSmash(features: SwingFeatures): SwingType {
        // 上手发球特征：az 持续向上（负值），旋转角度大
        if (features.avgAz < highServeAzThreshold &&
            features.rotationAngle > rotationAngleThreshold * 2) {
            return SwingType.SERVE
        }

        // 高压球：类似发球但可能在非发球局
        return SwingType.SERVE
    }

    /**
     * 分类常规击球（正手、反手、切削）
     */
    private fun classifyNormalShot(features: SwingFeatures): SwingType {
        // 切削判断：波形平缓（峰度低），旋转速度慢
        if (features.kurtosis < kurtosisThreshold &&
            features.maxAngularVelocity < 3.0f) {
            return SwingType.SLICE
        }

        // 正手/反手判断：根据旋转方向
        return when (features.rotationDirection) {
            RotationDirection.CLOCKWISE -> SwingType.FOREHAND
            RotationDirection.COUNTER_CLOCKWISE -> SwingType.BACKHAND
            RotationDirection.UNKNOWN -> {
                // 旋转方向不明显时，根据加速度模式判断
                if (features.maxAcceleration > 12.0f) {
                    SwingType.FOREHAND  // 默认正手
                } else {
                    SwingType.SLICE     // 否则可能是切削
                }
            }
        }
    }

    /**
     * 计算置信度
     */
    private fun calculateConfidence(features: SwingFeatures, swingType: SwingType): Float {
        var confidence = 0.5f  // 基础置信度

        // 根据特征明显程度增加置信度
        when (swingType) {
            SwingType.FOREHAND, SwingType.BACKHAND -> {
                // 常规击球：旋转方向明显则置信度高
                if (features.rotationDirection != RotationDirection.UNKNOWN) {
                    confidence += 0.2f
                }
                if (features.kurtosis > kurtosisThreshold) {
                    confidence += 0.1f
                }
            }

            SwingType.SLICE -> {
                // 切削：波形平缓则置信度高
                if (features.kurtosis < kurtosisThreshold) {
                    confidence += 0.2f
                }
            }

            SwingType.SERVE -> {
                // 发球：时长和旋转角度明显则置信度高
                if (features.durationMs > serveDurationThreshold) {
                    confidence += 0.2f
                }
                if (features.rotationAngle > rotationAngleThreshold * 2) {
                    confidence += 0.1f
                }
            }

            SwingType.FOREHAND_VOLLEY, SwingType.BACKHAND_VOLLEY -> {
                // 截击：时长短促则置信度高
                if (features.durationMs < volleyDurationThreshold) {
                    confidence += 0.2f
                }
            }

            else -> {
                confidence = 0.3f
            }
        }

        return confidence.coerceIn(0.0f, 1.0f)
    }

    /**
     * 生成 AI 建议
     */
    private fun generateSuggestion(features: SwingFeatures, swingType: SwingType): String? {
        return when (swingType) {
            SwingType.FOREHAND -> {
                when {
                    features.maxAngularVelocity < 2.0f -> "建议加快挥拍速度"
                    features.kurtosis < 0.3f -> "击球点不够集中，注意甜区击球"
                    else -> null
                }
            }

            SwingType.BACKHAND -> {
                when {
                    features.rotationAngle < 1.0f -> "引拍幅度不足"
                    features.maxAcceleration < 10.0f -> "击球力量偏弱"
                    else -> null
                }
            }

            SwingType.SLICE -> {
                when {
                    features.durationMs > 500 -> "切削动作过慢"
                    features.maxAngularVelocity > 4.0f -> "切削过猛，建议控制力度"
                    else -> null
                }
            }

            SwingType.SERVE -> {
                when {
                    features.durationMs < 500 -> "发球动作过急"
                    features.avgAz > -3.0f -> "抛球高度可能不足"
                    else -> null
                }
            }

            SwingType.FOREHAND_VOLLEY, SwingType.BACKHAND_VOLLEY -> {
                when {
                    features.durationMs > 350 -> "截击动作幅度过大"
                    features.maxAcceleration > 20.0f -> "截击过猛，注意卸力"
                    else -> null
                }
            }

            else -> null
        }
    }

    /**
     * 完整处理流程：从数据窗口到击球事件
     */
    fun processWindow(
        dataWindow: List<SensorDataPoint>,
        sessionId: Long,
        userId: Long
    ): SwingEvent? {
        if (dataWindow.isEmpty()) return null

        // 提取特征
        val extractor = FeatureExtractor()
        val features = extractor.extractFeatures(dataWindow)

        // 分类
        val result = classify(features)

        // 计算速度
        val speedCalculator = SpeedCalculator()
        val speeds = speedCalculator.calculateSpeed(dataWindow)

        // 质量评分
        val qualityScore = calculateQualityScore(features, result.swingType)

        return SwingEvent(
            swingType = result.swingType,
            maxSpeed = speeds.maxSpeed,
            avgSpeed = speeds.avgSpeed,
            startTime = dataWindow.first().timestamp,
            endTime = dataWindow.last().timestamp,
            duration = dataWindow.last().timestamp - dataWindow.first().timestamp,
            qualityScore = qualityScore,
            confidence = result.confidence,
            dataPoints = dataWindow,
            aiSuggestion = result.suggestion
        )
    }

    /**
     * 计算动作质量评分
     */
    private fun calculateQualityScore(features: SwingFeatures, swingType: SwingType): Float {
        var score = 50f  // 基础分

        // 根据动作特征加分/减分
        when (swingType) {
            SwingType.FOREHAND, SwingType.BACKHAND -> {
                // 常规击球：旋转流畅、力量适中获得高分
                if (features.rotationAngle in 1.5f..3.0f) score += 20f
                if (features.maxAcceleration in 10f..20f) score += 15f
                if (features.kurtosis > 0.5f) score += 10f  // 击球集中
            }

            SwingType.SLICE -> {
                // 切削：动作平稳获得高分
                if (features.stdAcceleration < 3.0f) score += 20f
                if (features.durationMs in 400f..600f) score += 15f
            }

            SwingType.SERVE -> {
                // 发球：旋转角度大、时长充足获得高分
                if (features.rotationAngle > 2.5f) score += 20f
                if (features.durationMs > 600f) score += 15f
                if (features.maxAngularVelocity > 4.0f) score += 10f
            }

            SwingType.FOREHAND_VOLLEY, SwingType.BACKHAND_VOLLEY -> {
                // 截击：短促有力获得高分
                if (features.durationMs < 250f) score += 20f
                if (features.maxAcceleration in 8f..15f) score += 15f
            }

            else -> score = 30f
        }

        return score.coerceIn(0f, 100f)
    }
}

/**
 * 分类结果
 */
data class ClassificationResult(
    val swingType: SwingType,
    val confidence: Float,
    val suggestion: String?
)
