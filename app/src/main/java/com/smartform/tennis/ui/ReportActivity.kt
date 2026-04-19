package com.smartform.tennis.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

        // 击球分布
        displayShotDistribution(session)
    }

    private fun displayShotDistribution(session: TrainingSessionEntity) {
        // 显示击球分布列表
        binding.chartPlaceholder.text = buildString {
            appendLine("击球分布:")
            appendLine("  正手：${session.forehandCount ?: 0}")
            appendLine("  反手：${session.backhandCount ?: 0}")
            appendLine("  切削：${session.sliceCount ?: 0}")
            appendLine("  发球：${session.serveCount ?: 0}")
            appendLine("  正手截击：${session.forehandVolleyCount ?: 0}")
            appendLine("  反手截击：${session.backhandVolleyCount ?: 0}")
        }
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
