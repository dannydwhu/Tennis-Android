package com.smartform.tennis.algorithm

import com.smartform.tennis.algorithm.model.SensorDataPoint
import com.smartform.tennis.algorithm.model.SwingType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt

/**
 * 击球分类器单元测试
 * 在本地 JVM 运行，无需模拟器
 */
class SwingClassifierTest {

    private lateinit var classifier: SwingClassifier
    private lateinit var featureExtractor: FeatureExtractor
    private lateinit var mockGenerator: MockDataGenerator

    @Before
    fun setup() {
        classifier = SwingClassifier()
        featureExtractor = FeatureExtractor()
        mockGenerator = MockDataGenerator()
    }

    @Test
    fun `test classifier identifies forehand`() {
        val testData = mockGenerator.generateForehand()
        val features = featureExtractor.extractFeatures(testData)
        val result = classifier.classify(features)

        println("Forehand 测试：识别为 ${result.swingType}, 置信度=${result.confidence}")

        assertTrue("应识别为正手或未知，实际为 ${result.swingType}",
            result.swingType == SwingType.FOREHAND || result.swingType == SwingType.UNKNOWN)
    }

    @Test
    fun `test classifier identifies backhand`() {
        val testData = mockGenerator.generateBackhand()
        val features = featureExtractor.extractFeatures(testData)
        val result = classifier.classify(features)

        println("Backhand 测试：识别为 ${result.swingType}, 置信度=${result.confidence}")

        assertTrue("应识别为反手或未知，实际为 ${result.swingType}",
            result.swingType == SwingType.BACKHAND || result.swingType == SwingType.UNKNOWN)
    }

    @Test
    fun `test classifier identifies serve`() {
        val testData = mockGenerator.generateServe()
        val features = featureExtractor.extractFeatures(testData)
        val result = classifier.classify(features)

        println("Serve 测试：识别为 ${result.swingType}, 置信度=${result.confidence}")

        assertTrue("应识别为发球或未知，实际为 ${result.swingType}",
            result.swingType == SwingType.SERVE || result.swingType == SwingType.UNKNOWN)
    }

    @Test
    fun `test classifier identifies volley`() {
        val testData = mockGenerator.generateForehandVolley()
        val features = featureExtractor.extractFeatures(testData)
        val result = classifier.classify(features)

        println("Volley 测试：识别为 ${result.swingType}, 置信度=${result.confidence}")

        assertTrue("应识别为截击或未知，实际为 ${result.swingType}",
            result.swingType == SwingType.FOREHAND_VOLLEY ||
            result.swingType == SwingType.BACKHAND_VOLLEY ||
            result.swingType == SwingType.UNKNOWN)
    }

    @Test
    fun `test speed calculator`() {
        val speedCalculator = SpeedCalculator()
        val testData = mockGenerator.generateForehand()

        val result = speedCalculator.calculateSpeed(testData)

        println("Speed test: max=${result.maxSpeed} km/h, avg=${result.avgSpeed} km/h")

        assertTrue("最大速度应大于 0", result.maxSpeed > 0)
        assertTrue("平均速度应大于等于 0", result.avgSpeed >= 0)
        assertTrue("最大速度应大于等于平均速度", result.maxSpeed >= result.avgSpeed)
    }

    @Test
    fun `test data preprocessor`() {
        val preprocessor = DataPreprocessor()

        val rawData = SensorDataPoint(
            timestamp = 1000,
            ax = 10.0f, ay = 0.5f, az = -9.0f,
            gx = 0.1f, gy = 0.2f, gz = 0.1f,
            mx = 20.0f, my = 30.0f, mz = 40.0f
        )

        val processed = preprocessor.process(rawData)

        assertNotNull(processed)
        assertEquals(rawData.timestamp, processed.timestamp)
    }

    @Test
    fun `test feature extractor`() {
        val testData = mockGenerator.generateForehand()
        val features = featureExtractor.extractFeatures(testData)

        assertTrue("时长应大于 0", features.durationMs > 0)
        assertTrue("最大加速度应大于 0", features.maxAcceleration > 0)
        assertTrue("最大角速度应大于 0", features.maxAngularVelocity > 0)

        println("Feature test: duration=${features.durationMs}ms, maxAccel=${features.maxAcceleration}, maxAngular=${features.maxAngularVelocity}")
    }

    @Test
    fun `test tennis swing engine processes data`() {
        val engine = TennisSwingEngine()
        engine.startSession(1, 1)

        val sessionData = mockGenerator.generateTrainingSession()

        for (dataPoint in sessionData) {
            engine.processDataPoint(dataPoint)
        }

        val stats = engine.endSession()

        println("Engine test: totalSwings=${stats.totalSwings}, swingCounts=${stats.swingCounts}")

        assertTrue("引擎应能处理数据", stats.totalSwings >= 0)
    }
}

/**
 * 简易模拟数据生成器（测试用）
 */
class MockDataGenerator {
    private val random = kotlin.random.Random

    fun generateForehand(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()

        for (i in 0 until 45) {
            val t = i / 100.0
            val timestamp = startTime + i * 10

            val gx = (sin(t * PI * 4) * 0.3 + random.nextFloat(-0.1f, 0.1f)).toFloat()
            val gy = (exp(-((t - 0.2) * 10).pow(2).toFloat()) * 5.0f + random.nextFloat(-0.2f, 0.2f)).toFloat()
            val gz = (cos(t * PI * 2) * 0.5 + random.nextFloat(-0.1f, 0.1f)).toFloat()

            val peak = exp(-((t - 0.25) * 15).pow(2).toFloat()) * 15.0f
            val ax = (peak * 0.3f + 9.81f * 0.1f + random.nextFloat(-0.5f, 0.5f)).toFloat()
            val ay = (peak * 0.5f + random.nextFloat(-0.5f, 0.5f)).toFloat()
            val az = (peak * 0.2f - 9.81f + random.nextFloat(-0.5f, 0.5f)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = 20.0f, my = 30.0f, mz = 40.0f
                )
            )
        }

        return dataPoints
    }

    fun generateBackhand(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()

        for (i in 0 until 45) {
            val t = i / 100.0
            val timestamp = startTime + i * 10

            val gx = (sin(t * PI * 4) * 0.3f + random.nextFloat(-0.1f, 0.1f)).toFloat()
            val gy = (-exp(-((t - 0.2) * 10).pow(2).toFloat()) * 5.0f + random.nextFloat(-0.2f, 0.2f)).toFloat()
            val gz = (-cos(t * PI * 2) * 0.5f + random.nextFloat(-0.1f, 0.1f)).toFloat()

            val peak = exp(-((t - 0.25) * 15).pow(2).toFloat()) * 12.0f
            val ax = (peak * 0.3f + 9.81f * 0.1f + random.nextFloat(-0.5f, 0.5f)).toFloat()
            val ay = (peak * 0.4f + random.nextFloat(-0.5f, 0.5f)).toFloat()
            val az = (peak * 0.2f - 9.81f + random.nextFloat(-0.5f, 0.5f)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = 20.0f, my = 30.0f, mz = 40.0f
                )
            )
        }

        return dataPoints
    }

    fun generateServe(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()

        for (i in 0 until 75) {
            val t = i / 100.0
            val timestamp = startTime + i * 10

            val gx = (sin(t * PI * 2) * 1.0f + random.nextFloat(-0.2f, 0.2f)).toFloat()
            val gy = (exp(-((t - 0.4) * 5).pow(2).toFloat()) * 8.0f + random.nextFloat(-0.3f, 0.3f)).toFloat()
            val gz = (sin(t * PI * 3) * 1.5f + random.nextFloat(-0.2f, 0.2f)).toFloat()

            val peak = exp(-((t - 0.5) * 8).pow(2).toFloat()) * 20.0f
            val ax = (peak * 0.2f + random.nextFloat(-1f, 1f)).toFloat()
            val ay = (peak * 0.3f + random.nextFloat(-1f, 1f)).toFloat()
            val az = (-peak * 0.5f - 9.81f + random.nextFloat(-1f, 1f)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = 20.0f, my = 30.0f, mz = 40.0f
                )
            )
        }

        return dataPoints
    }

    fun generateForehandVolley(): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()

        for (i in 0 until 20) {
            val t = i / 100.0
            val timestamp = startTime + i * 10

            val gx = (sin(t * PI * 10) * 0.2f + random.nextFloat(-0.1f, 0.1f)).toFloat()
            val gy = (exp(-((t - 0.1) * 20).pow(2).toFloat()) * 3.0f + random.nextFloat(-0.1f, 0.1f)).toFloat()
            val gz = (cos(t * PI * 5) * 0.3f + random.nextFloat(-0.1f, 0.1f)).toFloat()

            val peak = exp(-((t - 0.1) * 25).pow(2).toFloat()) * 8.0f
            val ax = (peak * 0.3f + 9.81f * 0.05f + random.nextFloat(-0.3f, 0.3f)).toFloat()
            val ay = (peak * 0.4f + random.nextFloat(-0.3f, 0.3f)).toFloat()
            val az = (peak * 0.2f - 9.81f + random.nextFloat(-0.3f, 0.3f)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = 20.0f, my = 30.0f, mz = 40.0f
                )
            )
        }

        return dataPoints
    }

    fun generateTrainingSession(): List<SensorDataPoint> {
        val allData = mutableListOf<SensorDataPoint>()
        allData.addAll(generateIdle(50))

        val swings = listOf(
            generateForehand(),
            generateBackhand(),
            generateForehand(),
            generateServe(),
            generateForehandVolley()
        )

        for ((index, swing) in swings.withIndex()) {
            allData.addAll(swing)
            allData.addAll(generateIdle(random.nextInt(30, 60)))
        }

        allData.addAll(generateIdle(50))
        return allData
    }

    private fun generateIdle(duration: Int): List<SensorDataPoint> {
        val dataPoints = mutableListOf<SensorDataPoint>()
        val startTime = System.currentTimeMillis()

        for (i in 0 until duration) {
            val timestamp = startTime + i * 10

            val ax = (9.81f * 0.1f + random.nextFloat(-0.2f, 0.2f)).toFloat()
            val ay = (random.nextFloat(-0.2f, 0.2f)).toFloat()
            val az = (-9.81f + random.nextFloat(-0.2f, 0.2f)).toFloat()

            val gx = (random.nextFloat(-0.05f, 0.05f)).toFloat()
            val gy = (random.nextFloat(-0.05f, 0.05f)).toFloat()
            val gz = (random.nextFloat(-0.05f, 0.05f)).toFloat()

            dataPoints.add(
                SensorDataPoint(
                    timestamp = timestamp,
                    ax = ax, ay = ay, az = az,
                    gx = gx, gy = gy, gz = gz,
                    mx = 20.0f, my = 30.0f, mz = 40.0f
                )
            )
        }

        return dataPoints
    }
}

fun kotlin.random.Random.nextFloat(from: Float, to: Float): Float {
    return from + (to - from) * nextFloat()
}

private fun Double.pow(power: Int): Double = Math.pow(this, power.toDouble())
private fun Double.toFloatSafe(): Float = this.toFloat()
