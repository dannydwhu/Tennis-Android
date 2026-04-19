package com.smartform.tennis.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smartform.tennis.R
import com.smartform.tennis.ui.fragment.DataFragment
import com.smartform.tennis.ui.fragment.LiveFragment

/**
 * 主页 Activity - 底部导航
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var bottomNavigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate 开始")
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_main)
            Log.d(TAG, "setContentView 完成")

            // 初始化底部导航
            bottomNavigation = findViewById(R.id.bottom_navigation)

            // 加载默认 Fragment (Live)
            if (savedInstanceState == null) {
                loadFragment(LiveFragment())
                Log.d(TAG, "LiveFragment 加载完成")
            }

            // 设置导航监听
            bottomNavigation?.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_live -> {
                        loadFragment(LiveFragment())
                        true
                    }
                    R.id.navigation_data -> {
                        loadFragment(DataFragment())
                        true
                    }
                    else -> false
                }
            }

            Log.d(TAG, "onCreate 完成")
        } catch (e: Exception) {
            Log.e(TAG, "onCreate 失败", e)
            throw e
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomNavigation = null
    }
}
