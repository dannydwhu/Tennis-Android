package com.smartform.tennis.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartform.tennis.databinding.ActivityDeviceBindBinding

/**
 * 设备绑定页
 */
class DeviceBindActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceBindBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBindBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // 返回
        binding.backButton.setOnClickListener {
            finish()
        }

        // 扫描设备
        binding.scanButton.setOnClickListener {
            // TODO: 实现蓝牙扫描
            Toast.makeText(this, "开始扫描设备...", Toast.LENGTH_SHORT).show()
            // 模拟扫描成功
            binding.scanButton.text = "已连接"
            binding.scanButton.isEnabled = false
            binding.nextButton.visibility = View.VISIBLE
        }

        // 跳过
        binding.skipButton.setOnClickListener {
            goToNext()
        }

        // 下一步
        binding.nextButton.setOnClickListener {
            goToNext()
        }
    }

    private fun goToNext() {
        startActivity(Intent(this, GuideActivity::class.java))
        finish()
    }
}
