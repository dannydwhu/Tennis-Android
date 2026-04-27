package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.smartform.tennis.databinding.FragmentProfileBinding

/**
 * PROFILE 页面 - 用户个人资料
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadMockUserData()
    }

    private fun setupUI() {
        // 设置点击事件
        binding.settingsCard.setOnClickListener {
            showToast("设置")
        }

        binding.syncCard.setOnClickListener {
            showToast("数据同步")
        }

        binding.feedbackCard.setOnClickListener {
            showToast("意见反馈")
        }

        binding.legalCard.setOnClickListener {
            showToast("法律条款")
        }

        binding.aboutCard.setOnClickListener {
            showToast("关于我们")
        }
    }

    private fun loadMockUserData() {
        // 模拟用户数据
        binding.nicknameText.text = "网球达人"
        binding.levelText.text = "Level 5"
        binding.trainingCountText.text = "156次"
        binding.bestScoreText.text = "186 km/h"
        binding.totalDaysText.text = "45天"
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
