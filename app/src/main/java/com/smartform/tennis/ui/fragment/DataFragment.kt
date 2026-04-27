package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.smartform.tennis.ui.screens.DataScreen

/**
 * DATA 页面 - 训练数据展示页面
 * 使用 Jetpack Compose 实现
 */
class DataFragment : Fragment() {

    private var isRefreshing by mutableStateOf(false)
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                DataScreen(
                    isRefreshing = isRefreshing,
                    onRefresh = { refreshData() }
                )
            }
        }
    }

    private fun refreshData() {
        isRefreshing = true
        handler.postDelayed({
            isRefreshing = false
        }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}
