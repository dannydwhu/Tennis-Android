package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartform.tennis.R
import com.smartform.tennis.databinding.FragmentLeaderboardBinding

/**
 * RANKING 页面 - 排行榜
 */
class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    // 模拟数据
    private var mockRankings = listOf<RankingItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadMockData()
    }

    private fun setupUI() {
        // Tab 切换 - 默认选中击球数榜
        binding.tabShots.isSelected = true
        binding.tabShots.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))

        setupTab(binding.tabShots)
        setupTab(binding.tabDuration)
        setupTab(binding.tabSpeed)
        setupTab(binding.tabTotal)
    }

    private fun setupTab(tabView: TextView) {
        tabView.setOnClickListener {
            // 切换选中状态
            resetTabs()

            tabView.isSelected = true
            tabView.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))

            // 根据选中项加载对应数据
            loadMockData()
        }
    }

    private fun resetTabs() {
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
        binding.tabShots.isSelected = false
        binding.tabDuration.isSelected = false
        binding.tabSpeed.isSelected = false
        binding.tabTotal.isSelected = false

        binding.tabShots.setTextColor(unselectedColor)
        binding.tabDuration.setTextColor(unselectedColor)
        binding.tabSpeed.setTextColor(unselectedColor)
        binding.tabTotal.setTextColor(unselectedColor)
    }

    private fun loadMockData() {
        // 模拟数据 - 实际应从 API 加载
        mockRankings = listOf(
            RankingItem("张三", "5,678 次", 1, true),
            RankingItem("李四", "5,234 次", 2, true),
            RankingItem("王五", "4,890 次", 3, false),
            RankingItem("赵六", "4,567 次", 4, false),
            RankingItem("钱七", "4,123 次", 5, true),
            RankingItem("孙八", "3,890 次", 6, false),
            RankingItem("周九", "3,567 次", 7, true),
            RankingItem("吴十", "3,234 次", 8, false)
        )

        // 设置冠军信息
        binding.championNickname.text = mockRankings.firstOrNull()?.nickname ?: "暂无"
        binding.championStats.text = "总击球数：${mockRankings.firstOrNull()?.stats ?: "--"}"

        // 更新 RecyclerView
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.rankingRecyclerView.adapter = RankingAdapter(mockRankings)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class RankingItem(
        val nickname: String,
        val stats: String,
        val rank: Int,
        val isUp: Boolean
    )
}
