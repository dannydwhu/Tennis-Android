package com.smartform.tennis.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.smartform.tennis.R
import com.smartform.tennis.databinding.ActivityGuideBinding

/**
 * 新手引导页（3步）
 */
class GuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuideBinding
    private val guides = listOf(
        GuideItem(R.drawable.ic_guide_sensor, "连接传感器", "通过蓝牙连接智能网球传感器，实时采集您的挥拍数据"),
        GuideItem(R.drawable.ic_guide_analyze, "智能分析", "AI 算法自动识别击球类型，记录击球速度和运动轨迹"),
        GuideItem(R.drawable.ic_guide_train, "科学训练", "根据您的水平定制训练计划，提升网球技能")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // 设置 ViewPager
        binding.guidePager.adapter = GuideAdapter(guides)

        // 设置指示器
        setupIndicators()

        // 监听页面切换
        binding.guidePager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
                updateButton(position)
            }
        })

        // 跳过
        binding.skipButton.setOnClickListener {
            goToHome()
        }

        // 下一步/进入首页
        binding.nextButton.setOnClickListener {
            val current = binding.guidePager.currentItem
            if (current < guides.size - 1) {
                binding.guidePager.currentItem = current + 1
            } else {
                goToHome()
            }
        }
    }

    private fun setupIndicators() {
        binding.indicatorContainer.removeAllViews()
        guides.forEach { _ ->
            val dot = ImageView(this)
            dot.setImageResource(R.drawable.indicator_dot)
            val params = LinearLayout.LayoutParams(12, 12)
            params.marginStart = 8
            params.marginEnd = 8
            dot.layoutParams = params
            binding.indicatorContainer.addView(dot)
        }
        updateIndicators(0)
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until binding.indicatorContainer.childCount) {
            val dot = binding.indicatorContainer.getChildAt(i) as ImageView
            dot.setColorFilter(
                ContextCompat.getColor(
                    this,
                    if (i == position) R.color.brand_primary else R.color.text_secondary
                )
            )
        }
    }

    private fun updateButton(position: Int) {
        binding.nextButton.text = if (position == guides.size - 1) "进入首页" else "下一步"
    }

    private fun goToHome() {
        // 跳转到首页
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    data class GuideItem(
        val imageRes: Int,
        val title: String,
        val desc: String
    )

    inner class GuideAdapter(private val items: List<GuideItem>) :
        androidx.recyclerview.widget.RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): GuideViewHolder {
            val view = layoutInflater.inflate(R.layout.item_guide, parent, false)
            return GuideViewHolder(view)
        }

        override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class GuideViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.guideImage)
            private val titleView: TextView = itemView.findViewById(R.id.guideTitle)
            private val descView: TextView = itemView.findViewById(R.id.guideDesc)

            fun bind(item: GuideItem) {
                imageView.setImageResource(item.imageRes)
                titleView.text = item.title
                descView.text = item.desc
            }
        }
    }
}
