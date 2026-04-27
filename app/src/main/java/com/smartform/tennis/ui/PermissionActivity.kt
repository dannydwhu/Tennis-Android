package com.smartform.tennis.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smartform.tennis.databinding.ActivityPermissionBinding

/**
 * 权限说明页
 */
class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // 返回
        binding.backButton.setOnClickListener {
            finish()
        }

        // 同意并继续 - 跳转到个人信息录入页
        binding.agreeButton.setOnClickListener {
            startActivity(Intent(this, ProfileSetupActivity::class.java))
            finish()
        }
    }
}
