//package com.smartform.tennis.ui
//
//import android.Manifest
//import android.bluetooth.BluetoothDevice
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.MenuItem
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.smartform.tennis.R
//import com.smartform.tennis.bluetooth.BluetoothManager
//import com.smartform.tennis.databinding.ActivityMainBinding
//import com.smartform.tennis.ui.fragment.AnalysisFragment
//import com.smartform.tennis.ui.fragment.DataFragment
//import com.smartform.tennis.ui.fragment.LeaderboardFragment
//import com.smartform.tennis.ui.fragment.LiveFragment
//import com.smartform.tennis.ui.fragment.ProfileFragment
//import com.smartform.tennis.ui.viewmodel.MainViewModel
//
///**
// * 主页 Activity
// *
// * 功能：
// * - 底部导航切换
// * - 蓝牙设备连接
// * - 实时显示击球数据
// * - 开始/结束训练
// */
//class MainActivity2 : AppCompatActivity() {
//
//    companion object {
//        private const val TAG = "MainActivity"
//    }
//
//    private lateinit var binding: ActivityMainBinding
//    private val viewModel: MainViewModel by viewModels()
//
//    private val fragments = mapOf(
//        R.id.navigation_live to LiveFragment(),
//        R.id.navigation_data to DataFragment(),
//        R.id.navigation_analysis to AnalysisFragment(),
//        R.id.navigation_leaderboard to LeaderboardFragment(),
//        R.id.navigation_profile to ProfileFragment()
//    )
//
//    private var currentFragmentId: Int = R.id.navigation_live
//
//    // 权限请求
//    private val permissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        val allGranted = permissions.values.all { it }
//        Log.d(TAG, "权限请求结果：$allGranted")
//        if (allGranted) {
//            viewModel.initBluetoothListener()
//            Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "需要蓝牙和位置权限", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(TAG, "onCreate 开始")
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        Log.d(TAG, "UI 初始化完成")
//
//        setupNavigation(savedInstanceState)
//        checkPermissions()
//
//        Log.d(TAG, "onCreate 完成")
//    }
//
//    private fun setupNavigation(savedInstanceState: Bundle?) {
//        Log.d(TAG, "设置底部导航")
//
//        // 设置默认选中项
//        binding.bottomNavigation.selectedItemId = R.id.navigation_live
//
//        // 监听导航项点击
//        binding.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
//            val fragmentId = item.itemId
//            if (fragmentId != currentFragmentId) {
//                loadFragment(fragmentId)
//                currentFragmentId = fragmentId
//                true
//            } else {
//                false
//            }
//        }
//
//        // 加载默认 Fragment
//        if (savedInstanceState == null) {
//            Log.d(TAG, "加载默认 LIVE Fragment")
//            loadFragment(R.id.navigation_live)
//        }
//    }
//
//    private fun loadFragment(fragmentId: Int) {
//        Log.d(TAG, "加载 Fragment: $fragmentId")
//        val fragment = fragments[fragmentId] ?: return
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.nav_host_fragment, fragment)
//            .commitAllowingStateLoss()
//    }
//
//    private fun checkPermissions() {
//        Log.d(TAG, "检查权限")
//
//        val permissions = mutableListOf(
//            Manifest.permission.BLUETOOTH,
//            Manifest.permission.BLUETOOTH_ADMIN
//        )
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
//            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
//            // Android 12+ 某些蓝牙操作仍可能需要位置权限
//            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//
//        val needRequest = permissions.any {
//            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
//        }
//
//        if (needRequest) {
//            Log.d(TAG, "需要请求权限，开始请求")
//            permissionLauncher.launch(permissions.toTypedArray())
//        } else {
//            Log.d(TAG, "权限已授予")
//            // 不在这里初始化蓝牙，只在用户主动操作时才初始化
//        }
//    }
//
//    private fun showDeviceList(devices: List<BluetoothDevice>) {
//        // 简化处理：连接第一个设备
//        // 实际应显示对话框让用户选择
//        if (devices.isNotEmpty()) {
//            val device = devices.first()
//            Toast.makeText(this, "发现设备：${device.name ?: device.address}", Toast.LENGTH_SHORT).show()
//            viewModel.connectDevice(device)
//        }
//    }
//}
