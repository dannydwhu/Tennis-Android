package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.smartform.tennis.ui.screens.AnalysisScreen

/**
 * ANALYSIS 页面 - 技术分析
 * 使用 Jetpack Compose 实现
 */
class AnalysisFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                AnalysisScreen(
                    onServeClick = { showToast("发球分析") },
                    onForehandClick = { showToast("正手分析") },
                    onBackhandClick = { showToast("反手分析") },
                    onUploadClick = { showToast("从相册选择视频") },
                    onRecordClick = { showToast("录制视频") },
                    onGetMoreClick = { showToast("获取更多分析次数") }
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}