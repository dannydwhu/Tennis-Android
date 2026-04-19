package com.smartform.tennis.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartform.tennis.TennisApplication
import com.smartform.tennis.algorithm.EngineStats
import com.smartform.tennis.algorithm.model.SwingType
import com.smartform.tennis.algorithm.TennisSwingEngine
import com.smartform.tennis.bluetooth.BluetoothDataConverter
import com.smartform.tennis.bluetooth.BluetoothManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 主页 ViewModel - 简化版本（仅 UI 展示）
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MainViewModel"
    }

    /**
     * 今日统计数据
     */
    data class TodayStats(
        val totalShots: Int = 0,
        val forehandCount: Int = 0,
        val backhandCount: Int = 0,
        val sliceCount: Int = 0,
        val serveCount: Int = 0,
        val forehandVolleyCount: Int = 0,
        val backhandVolleyCount: Int = 0,
        val maxSpeed: Float = 0f,
        val forehandMaxSpeed: Float = 0f,
        val backhandMaxSpeed: Float = 0f,
        val sliceMaxSpeed: Float = 0f,
        val serveMaxSpeed: Float = 0f,
        val forehandVolleyMaxSpeed: Float = 0f,
        val backhandVolleyMaxSpeed: Float = 0f
    )

    private val app = application as TennisApplication
    private val bluetoothManager = BluetoothManager(application)
    private val dataConverter = BluetoothDataConverter()
    private val swingEngine = TennisSwingEngine()

    init {
        Log.d(TAG, "ViewModel 初始化开始")
    }

    // 当前用户 ID（实际应从登录状态获取）
    private val currentUserId: Long = 1L

    // 当前会话 ID
    private var currentSessionId: Long = -1L

    // 蓝牙状态
    val bluetoothState = bluetoothManager.connectionState
        .stateIn(viewModelScope, SharingStarted.Lazily, BluetoothManager.BluetoothState.DISCONNECTED)

    // 算法统计
    val engineStats = swingEngine.statsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // UI 状态
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // 扫描结果
    val scanResults = bluetoothManager.scanResults
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * 开始扫描设备
     */
    fun startScan() {
        Log.d(TAG, "startScan")
        _uiState.update { it.copy(isScanning = true) }
        bluetoothManager.startScan()
    }

    /**
     * 停止扫描
     */
    fun stopScan() {
        Log.d(TAG, "stopScan")
        _uiState.update { it.copy(isScanning = false) }
        bluetoothManager.stopScan()
    }

    /**
     * 连接设备
     */
    fun connectDevice(device: android.bluetooth.BluetoothDevice) {
        Log.d(TAG, "connectDevice")
        _uiState.update { it.copy(isConnecting = true) }
        bluetoothManager.connect(device)
    }

    /**
     * 断开连接
     */
    fun disconnectDevice() {
        Log.d(TAG, "disconnectDevice")
        bluetoothManager.disconnect()
    }

    /**
     * 今日统计数据
     */
    private val _todayStats = MutableStateFlow(TodayStats())
    val todayStats: StateFlow<TodayStats> = _todayStats.asStateFlow()

    /**
     * 开始训练
     */
    fun startTraining() {
        Log.d(TAG, "startTraining")
        _uiState.update { it.copy(isTraining = true, sessionId = 1L) }
    }

    /**
     * 结束训练
     */
    fun stopTraining(sessionStats: EngineStats) {
        Log.d(TAG, "stopTraining")
        viewModelScope.launch {
            // 累加今日统计数据
            val currentStats = _todayStats.value
            val updatedStats = currentStats.copy(
                totalShots = currentStats.totalShots + sessionStats.totalSwings,
                forehandCount = currentStats.forehandCount + sessionStats.getCount(SwingType.FOREHAND),
                backhandCount = currentStats.backhandCount + sessionStats.getCount(SwingType.BACKHAND),
                sliceCount = currentStats.sliceCount + sessionStats.getCount(SwingType.SLICE),
                serveCount = currentStats.serveCount + sessionStats.getCount(SwingType.SERVE),
                forehandVolleyCount = currentStats.forehandVolleyCount + sessionStats.getCount(SwingType.FOREHAND_VOLLEY),
                backhandVolleyCount = currentStats.backhandVolleyCount + sessionStats.getCount(SwingType.BACKHAND_VOLLEY),
                maxSpeed = maxOf(currentStats.maxSpeed, sessionStats.maxSpeeds.values.maxOrNull() ?: 0f),
                forehandMaxSpeed = maxOf(currentStats.forehandMaxSpeed, sessionStats.getMaxSpeed(SwingType.FOREHAND)),
                backhandMaxSpeed = maxOf(currentStats.backhandMaxSpeed, sessionStats.getMaxSpeed(SwingType.BACKHAND)),
                sliceMaxSpeed = maxOf(currentStats.sliceMaxSpeed, sessionStats.getMaxSpeed(SwingType.SLICE)),
                serveMaxSpeed = maxOf(currentStats.serveMaxSpeed, sessionStats.getMaxSpeed(SwingType.SERVE)),
                forehandVolleyMaxSpeed = maxOf(currentStats.forehandVolleyMaxSpeed, sessionStats.getMaxSpeed(SwingType.FOREHAND_VOLLEY)),
                backhandVolleyMaxSpeed = maxOf(currentStats.backhandVolleyMaxSpeed, sessionStats.getMaxSpeed(SwingType.BACKHAND_VOLLEY))
            )
            _todayStats.value = updatedStats

            delay(100)

            _uiState.update {
                it.copy(
                    isTraining = false,
                    sessionId = -1L,
                    completedSessionId = 1L
                )
            }
        }
    }

    /**
     * 初始化蓝牙监听
     */
    fun initBluetoothListener() {
        Log.d(TAG, "initBluetoothListener")
        viewModelScope.launch {
            bluetoothManager.sensorDataFlow.collect { packet ->
                packet?.let { data ->
                    // 转换为算法可用格式
                    val dataPoint = dataConverter.convert(data)

                    // 添加到算法引擎
                    if (_uiState.value.isTraining) {
                        swingEngine.processDataPoint(dataPoint)
                    }
                }
            }
        }

        // 监听蓝牙连接状态
        viewModelScope.launch {
            bluetoothState.collect { state ->
                _uiState.update {
                    it.copy(
                        isConnecting = state == BluetoothManager.BluetoothState.CONNECTING,
                        isConnected = state == BluetoothManager.BluetoothState.CONNECTED
                    )
                }
            }
        }
    }

    /**
     * UI 状态
     */
    data class UiState(
        val isScanning: Boolean = false,
        val isConnecting: Boolean = false,
        val isConnected: Boolean = false,
        val isTraining: Boolean = false,
        val sessionId: Long = -1L,
        val completedSessionId: Long = -1L  // 已完成的会话 ID，用于跳转报告页
    )

    override fun onCleared() {
        Log.d(TAG, "onCleared")
        super.onCleared()
        bluetoothManager.disconnect()
        swingEngine.reset()
    }
}
