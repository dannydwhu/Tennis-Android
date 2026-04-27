package com.smartform.tennis.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.smartform.tennis.databinding.ActivityWelcomeBinding

/**
 * 欢迎页 - 首次启动显示
 */
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // 开始体验按钮 - 跳转到权限说明页
        binding.startButton.setOnClickListener {
            startActivity(Intent(this, PermissionActivity::class.java))
        }

        // 已有账号登录（暂不实现）
        binding.loginLink.setOnClickListener {
            // TODO: 实现登录功能
        }
    }
}
