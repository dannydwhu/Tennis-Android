package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smartform.tennis.R
import com.smartform.tennis.algorithm.TennisSwingEngine
import com.smartform.tennis.algorithm.model.SensorDataPoint
import com.smartform.tennis.algorithm.model.SwingType
import com.smartform.tennis.ui.viewmodel.MainViewModel
import java.util.Random

/**
 * 训练页面 - 实时训练数据展示
 */
class TrainingFragment : Fragment() {

    // 顶部栏
    private var timerText: TextView? = null

    // 速度显示
    private var maxSpeedProgressBar: ProgressBar? = null
    private var maxSpeedValueText: TextView? = null

    // 总击球数
    private var totalShotsText: TextView? = null

    // 击球统计 - 计数
    private var forehandCountText: TextView? = null
    private var backhandCountText: TextView? = null
    private var sliceCountText: TextView? = null
    private var serveCountText: TextView? = null
    private var forehandVolleyCountText: TextView? = null
    private var backhandVolleyCountText: TextView? = null

    // 击球统计 - 进度条
    private var forehandProgressBar: ProgressBar? = null
    private var backhandProgressBar: ProgressBar? = null
    private var sliceProgressBar: ProgressBar? = null
    private var serveProgressBar: ProgressBar? = null
    private var forehandVolleyProgressBar: ProgressBar? = null
    private var backhandVolleyProgressBar: ProgressBar? = null

    // 传感器数据日志
    private var sensorDataLogText: TextView? = null

    // 停止按钮
    private var stopTrainingButton: Button? = null

    // 训练状态
    private var isTraining = false
    private var hasStopped = false  // 防止重复停止
    private var startTime = 0L
    private val random = Random()

    // 日志缓冲区
    private val logBuffer = StringBuilder()
    private var logLineCount = 0
    private var lastLogUpdate = 0L

    // TennisSwingEngine
    private val swingEngine = TennisSwingEngine()
    private val handler = Handler(Looper.getMainLooper())
    private var contextRef: android.content.Context? = null

    // 模拟数据生成
    private var dataSequence = 0L
    private val BASE_TIMESTAMP = System.currentTimeMillis()

    // 当前模拟的击球类型
    private var currentSwingType: SwingType = SwingType.UNKNOWN
    private var swingInProgress = false
    private var swingStartTime = 0L

    // 计时器任务
    private val timerTask = object : Runnable {
        override fun run() {
            if (isTraining && timerText != null) {
                val elapsed = System.currentTimeMillis() - startTime
                val minutes = elapsed / 60000
                val seconds = (elapsed % 60000) / 1000
                timerText?.text = String.format("%02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
    }

    // 模拟训练运行任务
    private val trainingTask = object : Runnable {
        override fun run() {
            if (!isTraining) return

            try {
                // 生成模拟传感器数据
                val dataPoint = generateMockSensorData()

                // 发送到算法引擎处理
                val swingEvent = swingEngine.processDataPoint(dataPoint)

                // 记录传感器日志
                addSensorLog(dataPoint)

                // 如果有击球事件，更新 UI
                if (swingEvent != null && swingEvent.swingType != SwingType.UNKNOWN) {
                    updateStatsFromEngine(swingEvent.maxSpeed)
                    addSwingLog(swingEvent)
                }

                // 继续下一个数据点
                handler.postDelayed(this, 10) // 100Hz 频率
            } catch (e: Exception) {
                // 忽略异常，防止崩溃
            }
        }
    }

    /**
     * 生成模拟传感器数据 - 模拟 6 种不同的击球动作
     */
    private fun generateMockSensorData(): SensorDataPoint {
        dataSequence++
        val timestamp = BASE_TIMESTAMP + dataSequence * 10

        // 每 2 秒 (200 个数据点) 切换一种击球类型
        val swingCycle = (dataSequence - 1) / 200L
        val positionInCycle = (dataSequence - 1) % 200L

        // 在每個周期的开始选择新的击球类型
        if (positionInCycle == 0L) {
            currentSwingType = when ((swingCycle % 6).toInt()) {
                0 -> SwingType.FOREHAND
                1 -> SwingType.BACKHAND
                2 -> SwingType.SLICE
                3 -> SwingType.SERVE
                4 -> SwingType.FOREHAND_VOLLEY
                5 -> SwingType.BACKHAND_VOLLEY
                else -> SwingType.UNKNOWN
            }
            swingInProgress = true
            swingStartTime = timestamp
            addSwingTypeLog(currentSwingType)
        }

        // 在周期的 50-150 位置生成击球峰值（模拟挥拍动作）
        val isInSwing = positionInCycle in 50L..150L
        val swingProgress = if (isInSwing) (positionInCycle - 50L) / 100f else 0f

        // 生成基础噪声数据
        var ax = (random.nextFloat() - 0.5f) * 2f
        var ay = (random.nextFloat() - 0.5f) * 2f
        var az = -9.81f + (random.nextFloat() - 0.5f) * 2f
        var gx = (random.nextFloat() - 0.5f) * 0.5f
        var gy = (random.nextFloat() - 0.5f) * 0.5f
        var gz = (random.nextFloat() - 0.5f) * 0.5f

        // 如果正在挥拍，根据击球类型生成特征数据
        if (isInSwing) {
            // 使用正弦曲线模拟挥拍的加速度变化（开始小，中间大，结束小）
            val swingIntensity = kotlin.math.sin(swingProgress * kotlin.math.PI.toFloat())
            val baseStrength = 15f + random.nextFloat() * 15f // 15-30 的基础强度
            val peakStrength = baseStrength * swingIntensity

            when (currentSwingType) {
                SwingType.FOREHAND -> {
                    // 正手：X 轴正向加速度 + Y 轴正向角速度
                    ax = peakStrength * 1.2f
                    ay = peakStrength * 0.3f
                    gy = peakStrength * 0.6f
                    gz = -peakStrength * 0.2f
                }
                SwingType.BACKHAND -> {
                    // 反手：X 轴正向加速度 + Y 轴负向角速度
                    ax = peakStrength * 1.1f
                    ay = -peakStrength * 0.2f
                    gy = -peakStrength * 0.5f
                    gz = peakStrength * 0.2f
                }
                SwingType.SLICE -> {
                    // 切削：Y 轴正向加速度 + Z 轴旋转（侧旋）
                    ax = peakStrength * 0.4f
                    ay = peakStrength * 0.8f
                    az = peakStrength * 0.3f
                    gz = peakStrength * 0.7f
                }
                SwingType.SERVE -> {
                    // 发球：Z 轴正向加速度（向上挥拍）+ X 轴旋转
                    ax = peakStrength * 0.5f
                    az = peakStrength * 1.5f
                    gx = peakStrength * 0.8f
                    gy = -peakStrength * 0.3f
                }
                SwingType.FOREHAND_VOLLEY -> {
                    // 正手截击：较小的 X 轴加速度 + Y 轴正向角速度
                    ax = peakStrength * 0.7f
                    ay = peakStrength * 0.2f
                    gy = peakStrength * 0.4f
                }
                SwingType.BACKHAND_VOLLEY -> {
                    // 反手截击：较小的 X 轴加速度 + Y 轴负向角速度
                    ax = peakStrength * 0.6f
                    ay = -peakStrength * 0.2f
                    gy = -peakStrength * 0.35f
                }
                else -> {}
            }
        }

        return SensorDataPoint(
            timestamp = timestamp,
            ax = ax,
            ay = ay,
            az = az,
            gx = gx,
            gy = gy,
            gz = gz,
            mx = 20f + random.nextFloat() * 10f,
            my = 30f + random.nextFloat() * 10f,
            mz = 40f + random.nextFloat() * 10f
        )
    }

    /**
     * 添加击球类型切换日志
     */
    private fun addSwingTypeLog(swingType: SwingType) {
        val logLine = "\n====== 模拟：${swingType.displayName} ======\n"
        logBuffer.append(logLine)
        logLineCount++
        // 立即更新显示
        sensorDataLogText?.text = logBuffer.toString()
    }

    /**
     * 添加传感器数据日志
     */
    private fun addSensorLog(dataPoint: SensorDataPoint) {
        val accelMag = dataPoint.accelerationMagnitude

        val logLine = "[${dataPoint.timestamp}] " +
                "A(${String.format("%.1f", dataPoint.ax)}, ${String.format("%.1f", dataPoint.ay)}, ${String.format("%.1f", dataPoint.az)}) " +
                "G(${String.format("%.2f", dataPoint.gx)}, ${String.format("%.2f", dataPoint.gy)}, ${String.format("%.2f", dataPoint.gz)}) " +
                "|合力：${String.format("%.1f", accelMag)} m/s²\n"

        logBuffer.append(logLine)
        logLineCount++

        // 限制日志行数，避免内存溢出
        if (logLineCount > 50) {
            val lines = logBuffer.toString().split("\n")
            logBuffer.clear()
            logBuffer.append(lines.drop(lines.size - 40).joinToString("\n"))
            logLineCount = 40
        }

        // 每 100ms 实时更新一次 UI（10 个数据点）
        if (dataPoint.timestamp - lastLogUpdate >= 100) {
            sensorDataLogText?.text = logBuffer.toString()
            lastLogUpdate = dataPoint.timestamp
        }
    }

    /**
     * 添加击球识别日志
     */
    private fun addSwingLog(swingEvent: com.smartform.tennis.algorithm.model.SwingEvent) {
        val logLine = "\n*** 识别：${swingEvent.swingType.displayName} | " +
                "速度：${String.format("%.1f", swingEvent.maxSpeed)} km/h | " +
                "置信度：${String.format("%.0f%%", swingEvent.confidence * 100)} ***\n"

        logBuffer.append(logLine)
        logLineCount++
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_training, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化视图
        timerText = view.findViewById(R.id.timerText)

        maxSpeedProgressBar = view.findViewById(R.id.maxSpeedProgressBar)
        maxSpeedValueText = view.findViewById(R.id.maxSpeedValueText)

        totalShotsText = view.findViewById(R.id.totalShotsText)

        forehandCountText = view.findViewById(R.id.forehandCountText)
        backhandCountText = view.findViewById(R.id.backhandCountText)
        sliceCountText = view.findViewById(R.id.sliceCountText)
        serveCountText = view.findViewById(R.id.serveCountText)
        forehandVolleyCountText = view.findViewById(R.id.forehandVolleyCountText)
        backhandVolleyCountText = view.findViewById(R.id.backhandVolleyCountText)

        forehandProgressBar = view.findViewById(R.id.forehandProgressBar)
        backhandProgressBar = view.findViewById(R.id.backhandProgressBar)
        sliceProgressBar = view.findViewById(R.id.sliceProgressBar)
        serveProgressBar = view.findViewById(R.id.serveProgressBar)
        forehandVolleyProgressBar = view.findViewById(R.id.forehandVolleyProgressBar)
        backhandVolleyProgressBar = view.findViewById(R.id.backhandVolleyProgressBar)

        sensorDataLogText = view.findViewById(R.id.sensorDataLogText)

        stopTrainingButton = view.findViewById(R.id.stopTrainingButton)

        setupUI()
    }

    private fun setupUI() {
        stopTrainingButton?.setOnClickListener {
            stopTraining()
        }
    }

    fun startTraining() {
        isTraining = true
        startTime = System.currentTimeMillis()
        dataSequence = 0L
        lastLogUpdate = startTime
        contextRef = context

        // 重置引擎并重新开始
        swingEngine.reset()
        swingEngine.startSession(System.currentTimeMillis(), 1L)

        // 重置计数
        totalShotsText?.text = "0"
        maxSpeedValueText?.text = "0"
        maxSpeedProgressBar?.progress = 0

        forehandCountText?.text = "0"
        backhandCountText?.text = "0"
        sliceCountText?.text = "0"
        serveCountText?.text = "0"
        forehandVolleyCountText?.text = "0"
        backhandVolleyCountText?.text = "0"

        forehandProgressBar?.progress = 0
        backhandProgressBar?.progress = 0
        sliceProgressBar?.progress = 0
        serveProgressBar?.progress = 0
        forehandVolleyProgressBar?.progress = 0
        backhandVolleyProgressBar?.progress = 0

        // 初始化日志
        logBuffer.clear()
        logLineCount = 0
        sensorDataLogText?.text = "开始训练...\n"

        // 开始计时器
        handler.post(timerTask)

        // 开始模拟数据生成
        handler.post(trainingTask)
    }

    fun stopTraining() {
        // 防止重复执行
        if (!isTraining || hasStopped) return
        hasStopped = true
        isTraining = false

        // 立即停止所有 handler 回调
        handler.removeCallbacksAndMessages(null)

        // 结束会话
        val finalStats = swingEngine.endSession()
        val totalShots = finalStats.totalSwings
        val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()

        // 使用 activity 启动 ReportActivity，确保正确的返回栈
        val activity = activity
        if (activity != null && !activity.isFinishing && !activity.isDestroyed) {
            try {
                val viewModel = ViewModelProvider(activity)[MainViewModel::class.java]
                viewModel.stopTraining(finalStats)

                Toast.makeText(activity, "训练结束 - 总击球：$totalShots", Toast.LENGTH_SHORT).show()

                // 跳转到训练报告页面
                val intent = android.content.Intent(activity, com.smartform.tennis.ui.ReportActivity::class.java).apply {
                    putExtra(com.smartform.tennis.ui.ReportActivity.EXTRA_SESSION_ID, 1L)
                    putExtra(com.smartform.tennis.ui.ReportActivity.EXTRA_TOTAL_SHOTS, totalShots)
                    putExtra(com.smartform.tennis.ui.ReportActivity.EXTRA_MAX_SPEED, finalStats.maxSpeeds.values.maxOrNull() ?: 0f)
                    putExtra(com.smartform.tennis.ui.ReportActivity.EXTRA_DURATION, duration)
                    // 传递最多的击球类型
                    val maxSwingEntry = finalStats.swingCounts.maxByOrNull { it.value }
                    if (maxSwingEntry != null) {
                        putExtra(com.smartform.tennis.ui.ReportActivity.EXTRA_SWING_TYPE, maxSwingEntry.key.ordinal)
                        putExtra(com.smartform.tennis.ui.ReportActivity.EXTRA_SWING_COUNT, maxSwingEntry.value)
                    } else {
                        putExtra(com.smartform.tennis.ui.ReportActivity.EXTRA_SWING_TYPE, SwingType.FOREHAND.ordinal)
                        putExtra(com.smartform.tennis.ui.ReportActivity.EXTRA_SWING_COUNT, 0)
                    }
                }
                startActivity(intent)

                // 延迟弹出 Fragment，确保 Activity 启动完成
                handler.postDelayed({
                    try {
                        if (parentFragmentManager.backStackEntryCount > 0) {
                            parentFragmentManager.popBackStackImmediate()
                        }
                    } catch (e: Exception) {
                        // 忽略异常
                    }
                }, 100)
            } catch (e: Exception) {
                // 忽略异常，避免崩溃
                try {
                    if (parentFragmentManager.backStackEntryCount > 0) {
                        parentFragmentManager.popBackStackImmediate()
                    }
                } catch (ex: Exception) {
                    // 忽略异常
                }
            }
        } else {
            // activity 不可用，直接 pop 返回
            try {
                if (parentFragmentManager.backStackEntryCount > 0) {
                    parentFragmentManager.popBackStackImmediate()
                }
            } catch (e: Exception) {
                // 忽略异常
            }
        }

        // 清除 context 引用
        contextRef = null
    }

    /**
     * 从引擎更新统计数据
     */
    private fun updateStatsFromEngine(maxSpeed: Float) {
        val stats = swingEngine.getCurrentStats()

        totalShotsText?.text = stats.totalSwings.toString()
        maxSpeedValueText?.text = maxSpeed.toInt().toString()
        maxSpeedProgressBar?.progress = (maxSpeed / 200f * 100).toInt()

        // 更新各击球类型计数
        updateStrokeCount(SwingType.FOREHAND, forehandCountText, forehandProgressBar)
        updateStrokeCount(SwingType.BACKHAND, backhandCountText, backhandProgressBar)
        updateStrokeCount(SwingType.SLICE, sliceCountText, sliceProgressBar)
        updateStrokeCount(SwingType.SERVE, serveCountText, serveProgressBar)
        updateStrokeCount(SwingType.FOREHAND_VOLLEY, forehandVolleyCountText, forehandVolleyProgressBar)
        updateStrokeCount(SwingType.BACKHAND_VOLLEY, backhandVolleyCountText, backhandVolleyProgressBar)
    }

    private fun updateStrokeCount(swingType: SwingType, countText: TextView?, progressBar: ProgressBar?) {
        val count = swingEngine.getCurrentStats().getCount(swingType)
        countText?.text = count.toString()
        progressBar?.progress = (count.coerceAtMost(100))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 确保停止训练（防止重复调用）
        if (isTraining) {
            isTraining = false
            handler.removeCallbacksAndMessages(null)
            swingEngine.endSession()
        }
        swingEngine.reset()
        contextRef = null
        timerText = null
        maxSpeedProgressBar = null
        maxSpeedValueText = null
        totalShotsText = null
        forehandCountText = null
        backhandCountText = null
        sliceCountText = null
        serveCountText = null
        forehandVolleyCountText = null
        backhandVolleyCountText = null
        forehandProgressBar = null
        backhandProgressBar = null
        sliceProgressBar = null
        serveProgressBar = null
        forehandVolleyProgressBar = null
        backhandVolleyProgressBar = null
        sensorDataLogText = null
        stopTrainingButton = null
    }
}
