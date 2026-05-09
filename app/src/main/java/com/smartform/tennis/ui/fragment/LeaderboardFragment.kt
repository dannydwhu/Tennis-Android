package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.smartform.tennis.TennisApplication
import com.smartform.tennis.data.network.ApiClient
import com.smartform.tennis.ui.screens.LeaderboardScreen
import com.smartform.tennis.ui.screens.RankItemUi
import kotlinx.coroutines.launch

/**
 * 排行榜页面 - 显示 Top 10 和我的排名
 * 使用 Jetpack Compose 实现
 */
class LeaderboardFragment : Fragment() {

    private val apiService = ApiClient().apiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                var selectedTab by remember { mutableIntStateOf(0) }
                val tabs = listOf("周榜", "月榜", "总榜")

                // API data states
                var top10 by remember { mutableStateOf(emptyList<RankItemUi>()) }
                var currentUser by remember { mutableStateOf<RankItemUi?>(null) }
                var thirdPlaceScore by remember { mutableIntStateOf(0) }
                var isLoading by remember { mutableStateOf(true) }

                val timeRange = when (selectedTab) {
                    0 -> "week"
                    1 -> "month"
                    else -> "all"
                }

                // Load data from API
                LaunchedEffect(selectedTab) {
                    isLoading = true
                    loadLeaderboardData(timeRange) { entries, myRank, thirdScore ->
                        top10 = entries
                        currentUser = myRank
                        thirdPlaceScore = thirdScore
                        isLoading = false
                    }
                }

                LeaderboardScreen(
                    isLoading = isLoading,
                    top10 = top10,
                    currentUser = currentUser,
                    thirdPlaceScore = thirdPlaceScore,
                    selectedTab = selectedTab,
                    tabs = tabs,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    }

    private fun loadLeaderboardData(
        timeRange: String,
        onResult: (List<RankItemUi>, RankItemUi?, Int) -> Unit
    ) {
        lifecycleScope.launch {
            try {
                // Get leaderboard data
                val leaderboardResponse = apiService.getTotalShotsLeaderboard(timeRange, 10)
                val entries = if (leaderboardResponse.isSuccessful && leaderboardResponse.body()?.code == 0) {
                    leaderboardResponse.body()!!.data?.mapIndexed { index, entry ->
                        RankItemUi(
                            rank = index + 1,
                            name = entry.nickname ?: "用户${entry.userId}",
                            score = entry.totalShots ?: 0,
                            avatar = ""
                        )
                    } ?: emptyList()
                } else {
                    emptyList()
                }

                // Get my rank
                val myRankResponse = apiService.getMyRank(TennisApplication.userId, timeRange)
                val myRank = if (myRankResponse.isSuccessful && myRankResponse.body()?.code == 0) {
                    val data = myRankResponse.body()!!.data
                    if (data != null) {
                        RankItemUi(
                            rank = data.totalShotsRank.toInt(),
                            name = "我",
                            score = data.totalShots,
                            avatar = "🧑"
                        )
                    } else null
                } else null

                val thirdScore = entries.getOrNull(2)?.score ?: 0
                onResult(entries, myRank, thirdScore)

            } catch (e: Exception) {
                Toast.makeText(context, "加载排行榜失败: ${e.message}", Toast.LENGTH_SHORT).show()
                onResult(emptyList(), null, 0)
            }
        }
    }
}
