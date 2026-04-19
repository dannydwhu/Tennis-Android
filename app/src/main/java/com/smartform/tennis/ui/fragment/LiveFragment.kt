package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.smartform.tennis.R
import com.smartform.tennis.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * LIVE 页面 - 首页展示
 */
class LiveFragment : Fragment() {

    // 顶部栏
    private var settingsButton: ImageView? = null
    private var notificationButton: ImageView? = null

    // 等级卡片
    private var levelProgressBar: ProgressBar? = null

    // 训练按钮
    private var startTrainingButton: Button? = null
    private var connectButton: Button? = null

    // 最新识别
    private var lastSwingResultText: TextView? = null

    // Tab 切换
    private var tabToday: TextView? = null
    private var tabHistory: TextView? = null

    // 击球统计 - 次数
    private var forehandText: TextView? = null
    private var backhandText: TextView? = null
    private var sliceText: TextView? = null
    private var serveText: TextView? = null
    private var forehandVolleyText: TextView? = null
    private var backhandVolleyText: TextView? = null
    private var totalShotsText: TextView? = null

    // 击球统计 - 速度
    private var forehandSpeedText: TextView? = null
    private var backhandSpeedText: TextView? = null
    private var sliceSpeedText: TextView? = null
    private var serveSpeedText: TextView? = null
    private var forehandVolleySpeedText: TextView? = null
    private var backhandVolleySpeedText: TextView? = null
    private var maxSpeedText: TextView? = null

    // ViewModel
    private var viewModel: MainViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_live, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 获取 ViewModel
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        // 初始化视图 - 顶部栏
        settingsButton = view.findViewById(R.id.settingsButton)
        notificationButton = view.findViewById(R.id.notificationButton)

        // 等级卡片
        levelProgressBar = view.findViewById(R.id.levelProgressBar)

        // 训练按钮
        startTrainingButton = view.findViewById(R.id.startTrainingButton)
        connectButton = view.findViewById(R.id.connectButton)

        // 最新识别
        lastSwingResultText = view.findViewById(R.id.lastSwingResultText)

        // Tab 切换
        tabToday = view.findViewById(R.id.tabToday)
        tabHistory = view.findViewById(R.id.tabHistory)

        // 击球统计 - 次数
        forehandText = view.findViewById(R.id.forehandText)
        backhandText = view.findViewById(R.id.backhandText)
        sliceText = view.findViewById(R.id.sliceText)
        serveText = view.findViewById(R.id.serveText)
        forehandVolleyText = view.findViewById(R.id.forehandVolleyText)
        backhandVolleyText = view.findViewById(R.id.backhandVolleyText)
        totalShotsText = view.findViewById(R.id.totalShotsText)

        // 击球统计 - 速度
        forehandSpeedText = view.findViewById(R.id.forehandSpeedText)
        backhandSpeedText = view.findViewById(R.id.backhandSpeedText)
        sliceSpeedText = view.findViewById(R.id.sliceSpeedText)
        serveSpeedText = view.findViewById(R.id.serveSpeedText)
        forehandVolleySpeedText = view.findViewById(R.id.forehandVolleySpeedText)
        backhandVolleySpeedText = view.findViewById(R.id.backhandVolleySpeedText)
        maxSpeedText = view.findViewById(R.id.maxSpeedText)

        setupUI()
        observeStats()
    }

    /**
     * 观察今日统计数据
     */
    private fun observeStats() {
        viewModel?.let { vm ->
            lifecycleScope.launch {
                vm.todayStats.collect { stats ->
                    updateStats(stats)
                }
            }
        }
    }

    private fun updateStats(stats: MainViewModel.TodayStats) {
        totalShotsText?.text = stats.totalShots.toString()
        maxSpeedText?.text = if (stats.maxSpeed > 0) "${stats.maxSpeed.toInt()} km/h" else "- km/h"

        forehandText?.text = stats.forehandCount.toString()
        backhandText?.text = stats.backhandCount.toString()
        sliceText?.text = stats.sliceCount.toString()
        serveText?.text = stats.serveCount.toString()
        forehandVolleyText?.text = stats.forehandVolleyCount.toString()
        backhandVolleyText?.text = stats.backhandVolleyCount.toString()

        // 更新速度显示
        forehandSpeedText?.text = if (stats.forehandMaxSpeed > 0) stats.forehandMaxSpeed.toInt().toString() else "-"
        backhandSpeedText?.text = if (stats.backhandMaxSpeed > 0) stats.backhandMaxSpeed.toInt().toString() else "-"
        sliceSpeedText?.text = if (stats.sliceMaxSpeed > 0) stats.sliceMaxSpeed.toInt().toString() else "-"
        serveSpeedText?.text = if (stats.serveMaxSpeed > 0) stats.serveMaxSpeed.toInt().toString() else "-"
        forehandVolleySpeedText?.text = if (stats.forehandVolleyMaxSpeed > 0) stats.forehandVolleyMaxSpeed.toInt().toString() else "-"
        backhandVolleySpeedText?.text = if (stats.backhandVolleyMaxSpeed > 0) stats.backhandVolleyMaxSpeed.toInt().toString() else "-"

        lastSwingResultText?.text = if (stats.totalShots > 0) "今日已训练 ${stats.totalShots} 次" else "等待训练开始..."

        // 设置等级进度条 (78%)
        levelProgressBar?.progress = 78
    }

    private fun setupUI() {
        // 顶部栏按钮
        settingsButton?.setOnClickListener {
            Toast.makeText(requireContext(), "设置", Toast.LENGTH_SHORT).show()
        }

        notificationButton?.setOnClickListener {
            Toast.makeText(requireContext(), "通知", Toast.LENGTH_SHORT).show()
        }

        // 连接按钮
        connectButton?.setOnClickListener {
            Toast.makeText(requireContext(), "连接设备", Toast.LENGTH_SHORT).show()
        }

        // 开始训练按钮 - 跳转到 TrainingFragment
        startTrainingButton?.setOnClickListener {
            val trainingFragment = TrainingFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, trainingFragment)
                .addToBackStack(null)
                .commit()

            // 在新 Fragment 中开始训练
            trainingFragment.startTraining()
        }

        // Tab 切换
        tabToday?.setOnClickListener {
            tabToday?.setTextColor(requireContext().getColor(R.color.brand_primary))
            tabToday?.setBackgroundResource(R.drawable.bg_tab_selected)
            tabHistory?.setTextColor(requireContext().getColor(R.color.text_secondary))
            tabHistory?.setBackgroundResource(android.R.color.transparent)
        }

        tabHistory?.setOnClickListener {
            tabHistory?.setTextColor(requireContext().getColor(R.color.brand_primary))
            tabHistory?.setBackgroundResource(R.drawable.bg_tab_selected)
            tabToday?.setTextColor(requireContext().getColor(R.color.text_secondary))
            tabToday?.setBackgroundResource(android.R.color.transparent)
            Toast.makeText(requireContext(), "历史数据功能开发中...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsButton = null
        notificationButton = null
        levelProgressBar = null
        totalShotsText = null
        maxSpeedText = null
        forehandText = null
        backhandText = null
        sliceText = null
        serveText = null
        forehandVolleyText = null
        backhandVolleyText = null
        forehandSpeedText = null
        backhandSpeedText = null
        sliceSpeedText = null
        serveSpeedText = null
        forehandVolleySpeedText = null
        backhandVolleySpeedText = null
        connectButton = null
        startTrainingButton = null
        lastSwingResultText = null
        tabToday = null
        tabHistory = null
    }
}
