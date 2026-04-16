package com.smartform.tennis.ui

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.smartform.tennis.R
import com.smartform.tennis.algorithm.model.SwingType
import com.smartform.tennis.bluetooth.BluetoothManager
import com.smartform.tennis.databinding.ActivityMainBinding
import com.smartform.tennis.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

/**
 * 主页 Activity
 *
 * 功能：
 * - 蓝牙设备连接
 * - 实时显示击球数据
 * - 开始/结束训练
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    // 权限请求
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.startScan()
        } else {
            Toast.makeText(this, "需要蓝牙和位置权限", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeState()
        checkPermissions()
    }

    private fun setupUI() {
        // 连接按钮点击
        binding.connectButton.setOnClickListener {
            if (binding.root.tag == "connected") {
                viewModel.disconnectDevice()
            } else {
                viewModel.startScan()
            }
        }

        // 开始训练按钮
        binding.startTrainingButton.setOnClickListener {
            viewModel.startTraining()
        }

        // 结束训练按钮
        binding.stopTrainingButton.setOnClickListener {
            viewModel.stopTraining()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        updateUI(state)
                    }
                }

                launch {
                    viewModel.bluetoothState.collect { state ->
                        updateConnectionState(state)
                    }
                }

                launch {
                    viewModel.engineStats.collect { stats ->
                        stats?.let { updateStats(it) }
                    }
                }

                launch {
                    viewModel.scanResults.collect { results ->
                        // 显示扫描结果对话框（简化处理，实际应显示列表）
                        if (results.isNotEmpty()) {
                            showDeviceList(results.map { it.device })
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(state: MainViewModel.UiState) {
        // 训练按钮状态
        binding.startTrainingButton.visibility =
            if (state.isTraining) View.GONE else View.VISIBLE
        binding.stopTrainingButton.visibility =
            if (state.isTraining) View.VISIBLE else View.GONE

        // 连接按钮状态
        binding.connectButton.text = when {
            state.isConnecting -> "连接中..."
            state.isConnected -> "断开连接"
            else -> "连接设备"
        }

        // 检查是否完成训练，跳转报告页
        if (state.completedSessionId > 0) {
            // TODO: 跳转到报告页面
            // ReportActivity.start(this, state.completedSessionId)
        }
    }

    private fun updateConnectionState(state: BluetoothManager.BluetoothState) {
        when (state) {
            BluetoothManager.BluetoothState.CONNECTED -> {
                binding.connectionStatusText.text = "已连接"
                binding.connectionStatusText.setTextColor(
                    ContextCompat.getColor(this, android.R.color.holo_green_dark)
                )
                binding.root.tag = "connected"
            }
            BluetoothManager.BluetoothState.CONNECTING -> {
                binding.connectionStatusText.text = "连接中..."
                binding.root.tag = "connecting"
            }
            else -> {
                binding.connectionStatusText.text = getString(R.string.home_connect_device)
                binding.connectionStatusText.setTextColor(
                    ContextCompat.getColor(this, android.R.color.darker_gray)
                )
                binding.root.tag = "disconnected"
            }
        }
    }

    private fun updateStats(stats: com.smartform.tennis.algorithm.EngineStats) {
        // 总击球数
        binding.totalShotsText.text = stats.totalSwings.toString()

        // 各类型击球计数
        binding.forehandText.text = stats.getCount(SwingType.FOREHAND).toString()
        binding.backhandText.text = stats.getCount(SwingType.BACKHAND).toString()
        binding.sliceText.text = stats.getCount(SwingType.SLICE).toString()
        binding.serveText.text = stats.getCount(SwingType.SERVE).toString()
        binding.forehandVolleyText.text = stats.getCount(SwingType.FOREHAND_VOLLEY).toString()
        binding.backhandVolleyText.text = stats.getCount(SwingType.BACKHAND_VOLLEY).toString()

        // 最大速度
        val maxSpeed = stats.maxSpeeds.values.maxOrNull() ?: 0f
        binding.maxSpeedText.text = String.format("%.1f km/h", maxSpeed)
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val needRequest = permissions.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needRequest) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            viewModel.initBluetoothListener()
        }
    }

    private fun showDeviceList(devices: List<BluetoothDevice>) {
        // 简化处理：连接第一个设备
        // 实际应显示对话框让用户选择
        if (devices.isNotEmpty()) {
            val device = devices.first()
            Toast.makeText(this, "发现设备：${device.name ?: device.address}", Toast.LENGTH_SHORT).show()
            viewModel.connectDevice(device)
        }
    }
}
