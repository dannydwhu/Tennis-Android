package com.smartform.tennis.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatters.ValueFormatter
import com.smartform.tennis.R
import com.smartform.tennis.data.local.entity.TrainingSessionEntity
import com.smartform.tennis.databinding.ActivityReportBinding
import kotlinx.coroutines.launch

/**
 * 训练报告 Activity
 *
 * 展示单次训练的完整报告
 */
class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private var sessionId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionId = intent.getLongExtra(EXTRA_SESSION_ID, -1)
        if (sessionId == -1L) {
            finish()
            return
        }

        setupUI()
        loadReportData()
    }

    private fun setupUI() {
        // 分享按钮
        binding.shareButton.setOnClickListener {
            shareReport()
        }
    }

    private fun loadReportData() {
        lifecycleScope.launch {
            // TODO: 从数据库加载训练报告数据
            // val session = database.trainingSessionDao().getById(sessionId)
            // val shots = database.shotDao().getBySessionIdList(sessionId)

            // 模拟数据
            val mockSession = TrainingSessionEntity(
                id = sessionId,
                userId = 1,
                startTime = System.currentTimeMillis() - 600000,
                endTime = System.currentTimeMillis(),
                durationSeconds = 600,
                totalShots = 50,
                forehandCount = 20,
                backhandCount = 15,
                sliceCount = 5,
                serveCount = 5,
                forehandVolleyCount = 3,
                backhandVolleyCount = 2,
                maxSpeed = 120.5,
                avgSpeed = 95.0,
                qualityScore = 75.0,
                deviceId = "device_1",
                isSynced = false
            )

            displayReport(mockSession)
        }
    }

    private fun displayReport(session: TrainingSessionEntity) {
        // 训练时长
        val minutes = session.durationSeconds / 60
        val seconds = session.durationSeconds % 60
        binding.durationText.text = String.format("%02d:%02d", minutes, seconds)

        // 总击球数
        binding.totalShotsText.text = session.totalShots.toString()

        // 最大速度
        binding.maxSpeedText.text = String.format("%.1f km/h", session.maxSpeed ?: 0.0)

        // 图表
        setupChart(session)
    }

    private fun setupChart(session: TrainingSessionEntity) {
        val chart = binding.shotTypeChart

        // 准备数据
        val entries = listOf(
            BarEntry(0f, session.forehandCount.toFloat()),
            BarEntry(1f, session.backhandCount.toFloat()),
            BarEntry(2f, session.sliceCount.toFloat()),
            BarEntry(3f, session.serveCount.toFloat()),
            BarEntry(4f, session.forehandVolleyCount.toFloat()),
            BarEntry(5f, session.backhandVolleyCount.toFloat())
        )

        val dataSet = BarDataSet(entries, "击球次数").apply {
            colors = listOf(
                getColor(R.color.shot_forehand),
                getColor(R.color.shot_backhand),
                getColor(R.color.shot_slice),
                getColor(R.color.shot_serve),
                getColor(R.color.shot_forehand_volley),
                getColor(R.color.shot_backhand_volley)
            )
            valueTextColor = getColor(android.R.color.black)
            valueTextSize = 12f
        }

        chart.data = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        chart.xAxis.apply {
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        0 -> "正手"
                        1 -> "反手"
                        2 -> "切削"
                        3 -> "发球"
                        4 -> "正截"
                        5 -> "反截"
                        else -> ""
                    }
                }
            }
            granularity = 1f
            labelRotationAngle = -45f
        }

        chart.axisLeft.apply {
            axisMinimum = 0f
        }

        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.invalidate()
    }

    private fun shareReport() {
        // TODO: 生成分享图片
        val shareText = """
            我的网球训练报告
            训练时长：${binding.durationText.text}
            总击球数：${binding.totalShotsText.text}
            最大速度：${binding.maxSpeedText.text}
            来自 Smartform Tennis
        """.trimIndent()

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "分享训练报告"))
    }

    companion object {
        private const val EXTRA_SESSION_ID = "extra_session_id"
    }
}
