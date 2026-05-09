package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.smartform.tennis.TennisApplication
import com.smartform.tennis.data.network.ApiClient
import com.smartform.tennis.ui.screens.ProfileScreen
import kotlinx.coroutines.launch

/**
 * PROFILE 页面 - 用户个人资料
 * 使用 Jetpack Compose 实现
 */
class ProfileFragment : Fragment() {

    private val apiService = ApiClient().apiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                // User data states
                var nickname by remember { mutableStateOf("加载中...") }
                var level by remember { mutableIntStateOf(1) }
                var trainingCount by remember { mutableIntStateOf(0) }
                var bestSpeed by remember { mutableDoubleStateOf(0.0) }
                var totalDays by remember { mutableIntStateOf(0) }
                var isLoading by remember { mutableStateOf(true) }

                // Load user data from API
                LaunchedEffect(Unit) {
                    loadUserProfile(
                        onSuccess = { name, lvl, count, speed, days ->
                            nickname = name
                            level = lvl
                            trainingCount = count
                            bestSpeed = speed
                            totalDays = days
                            isLoading = false
                        },
                        onError = {
                            isLoading = false
                        }
                    )
                }

                ProfileScreen(
                    nickname = nickname,
                    level = "Level $level",
                    trainingCount = trainingCount,
                    bestSpeed = bestSpeed,
                    totalDays = totalDays,
                    onSettingsClick = { showToast("设置") },
                    onSyncClick = { showToast("数据同步") },
                    onFeedbackClick = { showToast("意见反馈") },
                    onLegalClick = { showToast("法律条款") },
                    onAboutClick = { showToast("关于我们") }
                )
            }
        }
    }

    private fun loadUserProfile(
        onSuccess: (String, Int, Int, Double, Int) -> Unit,
        onError: () -> Unit
    ) {
        lifecycleScope.launch {
            try {
                // Get user profile
                val profileResponse = apiService.getCurrentUser(
                    "Bearer ${TennisApplication.accessToken}"
                )

                if (profileResponse.isSuccessful && profileResponse.body()?.code == 0) {
                    val user = profileResponse.body()!!.data
                    if (user != null) {
                        val name = user.nickname ?: "用户${user.id}"
                        val lvl = user.currentLevel ?: 1
                        val exp = user.experiencePoints ?: 0L
                        // Calculate training days (rough estimate: 1 day per 1000 exp)
                        val days = (exp / 1000).toInt().coerceAtLeast(1)

                        // Get user stats for max speed and training count
                        val statsResponse = apiService.getUserStats(
                            TennisApplication.userId,
                            "all"
                        )

                        var maxSpeed = 0.0
                        var sessions = 0
                        if (statsResponse.isSuccessful && statsResponse.body()?.code == 0) {
                            val stats = statsResponse.body()!!.data
                            maxSpeed = (stats?.get("maxSpeed") as? Number)?.toDouble() ?: 0.0
                            sessions = (stats?.get("trainingCount") as? Number)?.toInt() ?: (stats?.get("trainingCount") as? Number)?.toLong()?.toInt() ?: 0
                        }

                        onSuccess(name, lvl, sessions, maxSpeed, days)
                    } else {
                        onError()
                    }
                } else {
                    onError()
                }
            } catch (e: Exception) {
                onError()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
