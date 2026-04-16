package com.smartform.tennis.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smartform.tennis.databinding.ActivityLeaderboardBinding

/**
 * 排行榜 Activity
 *
 * 展示各类击球排行榜
 */
class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: 实现排行榜逻辑
        // 1. Tab 切换：总击球榜 / 周榜 / 速度榜
        // 2. RecyclerView 展示排名
        // 3. 从数据库或 API 获取数据
    }
}
