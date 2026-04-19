package com.smartform.tennis.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartform.tennis.databinding.ItemRankingBinding

class RankingAdapter(
    private val rankings: List<LeaderboardFragment.RankingItem>
) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemRankingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeaderboardFragment.RankingItem) {
            binding.rankText.text = item.rank.toString()
            binding.nicknameText.text = item.nickname
            binding.statsText.text = item.stats

            // 排名颜色
            val rankColor = when (item.rank) {
                1 -> itemView.context.getColor(com.smartform.tennis.R.color.gold)
                2 -> itemView.context.getColor(com.smartform.tennis.R.color.silver)
                3 -> itemView.context.getColor(com.smartform.tennis.R.color.bronze)
                else -> itemView.context.getColor(com.smartform.tennis.R.color.text_primary)
            }
            binding.rankText.setTextColor(rankColor)

            // 排名变化图标
            binding.rankChangeIcon.visibility = if (item.isUp) {
                android.view.View.VISIBLE
            } else {
                android.view.View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rankings[position])
    }

    override fun getItemCount(): Int = rankings.size
}
