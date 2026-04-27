package com.smartform.tennis.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartform.tennis.databinding.ActivityProfileSetupBinding

/**
 * 个人信息录入页
 */
class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private var selectedLevel: Int = 1 // 默认业余

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // 返回
        binding.backButton.setOnClickListener {
            finish()
        }

        // 选择网球水平
        binding.levelGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedLevel = when (checkedId) {
                binding.levelNewbie.id -> 0 // 新手
                binding.levelAmateur.id -> 1 // 业余
                binding.levelPro.id -> 2 // 高手
                binding.levelExpert.id -> 3 // 专业
                else -> 1
            }
        }

        // 默认选中业余
        binding.levelAmateur.isChecked = true

        // 下一步 - 跳转到设备绑定页
        binding.nextButton.setOnClickListener {
            val nickname = binding.nicknameInput.text.toString().trim()
            if (nickname.isEmpty()) {
                Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: 保存用户信息

            startActivity(Intent(this, DeviceBindActivity::class.java))
            finish()
        }
    }
}
