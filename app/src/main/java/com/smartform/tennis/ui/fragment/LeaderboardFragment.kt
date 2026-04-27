package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartform.tennis.R
import com.smartform.tennis.databinding.FragmentLeaderboardBinding

/**
 * 排行榜页面 - 显示 Top 10 和我的排名（固定底部）
 */
class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    // 排行榜数据
    private val top10 = mutableListOf<RankItem>()
    private var currentUser: RankItem? = null
    private var thirdPlaceScore: Int = 0
    private var currentTab: String = "week" // week, month, total

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
        // Tab 切换 - 默认选中周榜
        binding.tabShots.isSelected = true
        binding.tabShots.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))

        setupTab(binding.tabShots, "week")
        setupTab(binding.tabDuration, "month")
        setupTab(binding.tabTotal, "total")
    }

    private fun setupTab(tabView: TextView, tabType: String) {
        tabView.setOnClickListener {
            resetTabs()
            tabView.isSelected = true
            tabView.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))
            currentTab = tabType
            loadMockData()
        }
    }

    private fun resetTabs() {
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
        binding.tabShots.isSelected = false
        binding.tabDuration.isSelected = false
        binding.tabTotal.isSelected = false
        binding.tabShots.setTextColor(unselectedColor)
        binding.tabDuration.setTextColor(unselectedColor)
        binding.tabTotal.setTextColor(unselectedColor)
    }

    /**
     * 加载模拟数据（实际应从 API 加载）
     */
    private fun loadMockData() {
        // 示例数据 - 根据不同榜单显示不同数据
        top10.clear()

        when (currentTab) {
            "week" -> {
                // 周榜数据
                top10.addAll(listOf(
                    RankItem(1, "疯狂小白菜", 1400, "🥇"),
                    RankItem(2, "挥拍不止", 1200, "🥈"),
                    RankItem(3, "ACE10363", 1200, "🥉"),
                    RankItem(4, "ACE_masan", 1100, ""),
                    RankItem(5, "Faye Dong", 1100, ""),
                    RankItem(6, "ACE97630", 1100, ""),
                    RankItem(7, "ACE13757", 1000, ""),
                    RankItem(8, "刚学网球", 1000, ""),
                    RankItem(9, "网豆子", 943, ""),
                    RankItem(10, "ACE97657", 909, "")
                ))
                // 周榜：用户排名 15
                currentUser = RankItem(15, "我", 650, "🧑")
                thirdPlaceScore = 1200
            }
            "month" -> {
                // 月榜数据
                top10.addAll(listOf(
                    RankItem(1, "网球迷", 5800, "🥇"),
                    RankItem(2, "tennis_pro", 5200, "🥈"),
                    RankItem(3, "球王李", 4800, "🥉"),
                    RankItem(4, " AceQueen", 4500, ""),
                    RankItem(5, "网球达人", 4200, ""),
                    RankItem(6, "正手杀手", 3800, ""),
                    RankItem(7, "反手大师", 3500, ""),
                    RankItem(8, "削球高手", 3200, ""),
                    RankItem(9, "发球机器", 3000, ""),
                    RankItem(10, "截击王", 2800, "")
                ))
                // 月榜：用户排名 8
                currentUser = RankItem(8, "我", 2100, "🧑")
                thirdPlaceScore = 4800
            }
            "total" -> {
                // 总榜数据
                top10.addAll(listOf(
                    RankItem(1, "传奇球星", 25000, "🥇"),
                    RankItem(2, "网球教练", 22000, "🥈"),
                    RankItem(3, "运动达人", 20000, "🥉"),
                    RankItem(4, "全勤王", 18000, ""),
                    RankItem(5, "热爱网球", 16000, ""),
                    RankItem(6, "小球童", 15000, ""),
                    RankItem(7, "周末球手", 14000, ""),
                    RankItem(8, "业余高手", 12000, ""),
                    RankItem(9, "网球新人", 10000, ""),
                    RankItem(10, "初学者", 8000, "")
                ))
                // 总榜：用户排名 3（在 Top 10 内）
                currentUser = RankItem(3, "我", 20000, "🧑")
                thirdPlaceScore = 20000
            }
        }

        // 构建列表
        val me = currentUser
        if (me != null && me.rank > 10) {
            // 排名 > 10：底部固定显示，列表只显示 Top 10
            showMyRankAtBottom(me)
            binding.rankingRecyclerView.adapter = RankingAdapter(top10)
        } else {
            // 排名 1~10：高亮显示
            hideMyRankFixed()
            val highlightedList = top10.map { item ->
                if (item.rank == me?.rank) item.copy(isMe = true) else item
            }
            binding.rankingRecyclerView.adapter = RankingAdapter(highlightedList)
        }

        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * 在底部固定显示我的排名
     */
    private fun showMyRankAtBottom(me: RankItem) {
        binding.myRankFixedContainer.visibility = View.VISIBLE
        binding.myFixedRank.text = me.rank.toString()
        binding.myFixedAvatar.text = me.avatar
        binding.myFixedNickname.text = me.name
        binding.myFixedScore.text = "${me.score} 次"

        // 显示差距
        val gap = thirdPlaceScore - me.score
        if (gap > 0) {
            binding.myFixedGapText.visibility = View.VISIBLE
            binding.myFixedGapText.text = "距第3名还差 $gap 球"
        } else {
            binding.myFixedGapText.visibility = View.GONE
        }
    }

    /**
     * 隐藏底部固定的我
     */
    private fun hideMyRankFixed() {
        binding.myRankFixedContainer.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 排行榜项数据类
     */
    data class RankItem(
        val rank: Int,
        val name: String,
        val score: Int,
        val avatar: String = "",
        val isMe: Boolean = false
    )

    /**
     * 排行榜适配器
     */
    class RankingAdapter(private val items: List<RankItem>) :
        RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_leaderboard, parent, false)
            return RankingViewHolder(view)
        }

        override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val rankText: TextView = itemView.findViewById(R.id.rankText)
            private val avatarText: TextView = itemView.findViewById(R.id.avatarText)
            private val nicknameText: TextView = itemView.findViewById(R.id.nicknameText)
            private val dataText: TextView = itemView.findViewById(R.id.dataText)
            private val levelText: TextView = itemView.findViewById(R.id.levelText)

            fun bind(item: RankItem) {
                // 排名
                val rankDisplay = when (item.rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> item.rank.toString()
                }
                rankText.text = rankDisplay
                rankText.textSize = if (item.rank <= 3) 20f else 16f

                // 头像
                avatarText.text = item.avatar.ifEmpty { "🥎" }

                // 昵称
                nicknameText.text = if (item.isMe) "我" else item.name
                nicknameText.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        if (item.isMe) R.color.brand_primary else R.color.text_primary
                    )
                )

                // 击球数
                dataText.text = "${item.score} 次"

                // 头像背景色高亮当前用户
                if (item.isMe) {
                    avatarText.background.setTint(
                        ContextCompat.getColor(itemView.context, R.color.brand_primary_light)
                    )
                } else {
                    avatarText.background.setTint(
                        ContextCompat.getColor(itemView.context, R.color.bg_card)
                    )
                }

                // 如果是当前用户，背景高亮
                if (item.isMe) {
                    itemView.setBackgroundColor(
                        ContextCompat.getColor(itemView.context, R.color.brand_primary_light)
                    )
                } else {
                    itemView.setBackgroundColor(
                        ContextCompat.getColor(itemView.context, R.color.bg_card)
                    )
                }
            }
        }
    }
}
