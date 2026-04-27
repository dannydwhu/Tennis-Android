package com.smartform.tennis.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.smartform.tennis.R
import com.smartform.tennis.algorithm.model.SwingType
import com.smartform.tennis.algorithm.model.SwingEvent
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
    private var totalShots: Int = 0
    private var maxSpeed: Float = 0f
    private var durationSeconds: Int = 0
    private var swingType: SwingType = SwingType.FOREHAND
    private var swingCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 从 Intent 获取训练数据
        sessionId = intent.getLongExtra(EXTRA_SESSION_ID, -1)
        totalShots = intent.getIntExtra(EXTRA_TOTAL_SHOTS, 0)
        maxSpeed = intent.getFloatExtra(EXTRA_MAX_SPEED, 0f)
        durationSeconds = intent.getIntExtra(EXTRA_DURATION, 0)
        swingType = SwingType.values()[intent.getIntExtra(EXTRA_SWING_TYPE, SwingType.FOREHAND.ordinal)]
        swingCount = intent.getIntExtra(EXTRA_SWING_COUNT, 0)

        if (sessionId == -1L) {
            finish()
            return
        }

        setupUI()
        setupBackHandler()
        displayReport()
    }

    /**
     * 处理系统返回键
     */
    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 直接 finish，让系统处理返回栈
                finish()
            }
        })
    }

    private fun setupUI() {
        // 返回按钮
        binding.backButton.setOnClickListener {
            finish()
        }

        // 分享按钮
        binding.shareButton.setOnClickListener {
            shareReport()
        }

        // 查看详细数据按钮
        binding.viewDetailButton.setOnClickListener {
            // TODO: 跳转到数据详情页
        }

        // 查看排行榜按钮
        binding.viewRankingButton.setOnClickListener {
            // TODO: 跳转到排行榜页面
        }
    }

    private fun displayReport() {
        // 训练时长
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        binding.trainingDurationText.text = String.format("%02d:%02d", minutes, seconds)

        // 总击球数
        binding.totalShotsText.text = totalShots.toString()

        // 最大速度
        binding.maxSpeedText.text = "${maxSpeed.toInt()} km/h"

        // 击球类型和次数
        binding.strokeTypeText.text = when (swingType) {
            SwingType.FOREHAND -> "正手击球"
            SwingType.BACKHAND -> "反手击球"
            SwingType.SLICE -> "切削球"
            SwingType.SERVE -> "发球"
            SwingType.FOREHAND_VOLLEY -> "正手截击"
            SwingType.BACKHAND_VOLLEY -> "反手截击"
            else -> "击球"
        }
        binding.strokeCountText.text = "$swingCount 次"

        // AI 反馈
        binding.aiFeedbackText.text = generateAIFeedback()
    }

    private fun generateAIFeedback(): String {
        val feedback = StringBuilder()

        // 根据击球类型和数量生成反馈
        when (swingType) {
            SwingType.FOREHAND -> {
                feedback.append("您的正手击球稳定性良好。")
                if (maxSpeed > 100) {
                    feedback.append("球速出色，继续保持!")
                } else {
                    feedback.append("建议加强力量训练提升球速。")
                }
            }
            SwingType.BACKHAND -> {
                feedback.append("您的反手击球技术不错。")
                if (swingCount < 10) {
                    feedback.append("建议增加反手训练频次。")
                } else {
                    feedback.append("反手击球次数充足，保持训练。")
                }
            }
            SwingType.SLICE -> {
                feedback.append("您的切削球旋转控制良好。")
                feedback.append("可以尝试变化切削深度和角度。")
            }
            SwingType.SERVE -> {
                feedback.append("您的发球动作规范。")
                if (maxSpeed > 120) {
                    feedback.append("发球速度优秀!")
                } else {
                    feedback.append("建议加强抛球稳定性和腿部发力。")
                }
            }
            SwingType.FOREHAND_VOLLEY, SwingType.BACKHAND_VOLLEY -> {
                feedback.append("您的截击反应迅速。")
                feedback.append("继续保持网前意识和 timing。")
            }
            else -> {
                feedback.append("训练数据良好。")
                feedback.append("建议均衡练习各种击球类型。")
            }
        }

        return feedback.toString()
    }

    private fun shareReport() {
        val shareText = """
            我的网球训练报告
            训练时长：${binding.trainingDurationText.text}
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
        const val EXTRA_SESSION_ID = "extra_session_id"
        const val EXTRA_TOTAL_SHOTS = "extra_total_shots"
        const val EXTRA_MAX_SPEED = "extra_max_speed"
        const val EXTRA_DURATION = "extra_duration"
        const val EXTRA_SWING_TYPE = "extra_swing_type"
        const val EXTRA_SWING_COUNT = "extra_swing_count"
    }
}
