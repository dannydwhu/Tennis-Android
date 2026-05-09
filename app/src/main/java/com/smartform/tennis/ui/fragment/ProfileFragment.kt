package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.smartform.tennis.ui.screens.ProfileScreen

/**
 * PROFILE 页面 - 用户个人资料
 * 使用 Jetpack Compose 实现
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ProfileScreen(
                    onSettingsClick = { showToast("设置") },
                    onSyncClick = { showToast("数据同步") },
                    onFeedbackClick = { showToast("意见反馈") },
                    onLegalClick = { showToast("法律条款") },
                    onAboutClick = { showToast("关于我们") }
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}