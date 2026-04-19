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
        // 设备管理菜单
        binding.deviceMenuItem.setOnClickListener {
            showToast("设备管理")
        }

        // 设置菜单
        binding.settingsMenuItem.setOnClickListener {
            showToast("设置")
        }

        // 退出登录
        binding.logoutButton.setOnClickListener {
            showToast("退出登录")
        }
    }

    private fun loadMockUserData() {
        // 模拟用户数据
        binding.nicknameText.text = "网球爱好者"
        binding.userIdText.text = "ID: 10001"
        binding.levelText.text = "Lv.5"
        binding.expProgressBar.progress = 20
        binding.expText.text = "距离下一级还需 80 经验"
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
