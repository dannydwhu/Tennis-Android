package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.smartform.tennis.ui.screens.LeaderboardScreen

/**
 * 排行榜页面 - 显示 Top 10 和我的排名
 * 使用 Jetpack Compose 实现
 */
class LeaderboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                LeaderboardScreen()
            }
        }
    }
}