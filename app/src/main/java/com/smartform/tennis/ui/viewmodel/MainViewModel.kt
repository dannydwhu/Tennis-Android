package com.smartform.tennis.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartform.tennis.TennisApplication
import com.smartform.tennis.algorithm.TennisSwingEngine
import com.smartform.tennis.algorithm.model.SwingEvent
import com.smartform.tennis.algorithm.model.SwingType
import com.smartform.tennis.bluetooth.BluetoothDataConverter
import com.smartform.tennis.bluetooth.BluetoothManager
import com.smartform.tennis.data.local.entity.ShotEntity
import com.smartform.tennis.data.local.entity.TrainingSessionEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 主页 ViewModel
 *
 * 管理蓝牙连接、算法识别、数据存储
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as TennisApplication
    private val bluetoothManager = BluetoothManager(application)
    private val dataConverter = BluetoothDataConverter()
    private val swingEngine = TennisSwingEngine()

    private val database = app.database
    private val sessionDao = database.trainingSessionDao()
    private val shotDao = database.shotDao()
    private val sensorDataDao = database.sensorDataDao()

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
        _uiState.update { it.copy(isScanning = true) }
        bluetoothManager.startScan()
    }

    /**
     * 停止扫描
     */
    fun stopScan() {
        _uiState.update { it.copy(isScanning = false) }
        bluetoothManager.stopScan()
    }

    /**
     * 连接设备
     */
    fun connectDevice(device: android.bluetooth.BluetoothDevice) {
        _uiState.update { it.copy(isConnecting = true) }
        bluetoothManager.connect(device)
    }

    /**
     * 断开连接
     */
    fun disconnectDevice() {
        bluetoothManager.disconnect()
    }

    /**
     * 开始训练
     */
    fun startTraining() {
        viewModelScope.launch {
            // 创建新的训练会话
            val session = TrainingSessionEntity(
                id = System.currentTimeMillis(),
                userId = currentUserId,
                startTime = System.currentTimeMillis(),
                endTime = null,
                durationSeconds = 0,
                totalShots = 0,
                forehandCount = 0,
                backhandCount = 0,
                sliceCount = 0,
                serveCount = 0,
                forehandVolleyCount = 0,
                backhandVolleyCount = 0,
                maxSpeed = null,
                avgSpeed = null,
                qualityScore = null,
                deviceId = bluetoothManager.toString(),
                isSynced = false
            )

            sessionDao.insert(session)
            currentSessionId = session.id

            // 启动算法引擎
            swingEngine.startSession(currentSessionId, currentUserId)

            _uiState.update { it.copy(isTraining = true, sessionId = currentSessionId) }
        }
    }

    /**
     * 结束训练
     */
    fun stopTraining() {
        viewModelScope.launch {
            // 结束算法引擎
            val finalStats = swingEngine.endSession()

            // 更新会话记录
            val session = sessionDao.getById(currentSessionId)
            if (session != null) {
                val updatedSession = session.copy(
                    endTime = System.currentTimeMillis(),
                    durationSeconds = ((session.endTime ?: System.currentTimeMillis()) - session.startTime) / 1000,
                    totalShots = finalStats.totalSwings,
                    forehandCount = finalStats.getCount(SwingType.FOREHAND),
                    backhandCount = finalStats.getCount(SwingType.BACKHAND),
                    sliceCount = finalStats.getCount(SwingType.SLICE),
                    serveCount = finalStats.getCount(SwingType.SERVE),
                    forehandVolleyCount = finalStats.getCount(SwingType.FOREHAND_VOLLEY),
                    backhandVolleyCount = finalStats.getCount(SwingType.BACKHAND_VOLLEY),
                    maxSpeed = finalStats.maxSpeeds.values.maxOrNull()?.toDouble(),
                    isSynced = false
                )
                sessionDao.update(updatedSession)
            }

            _uiState.update {
                it.copy(
                    isTraining = false,
                    sessionId = -1L,
                    completedSessionId = currentSessionId
                )
            }

            currentSessionId = -1L
        }
    }

    /**
     * 初始化蓝牙监听
     */
    fun initBluetoothListener() {
        viewModelScope.launch {
            bluetoothManager.sensorDataFlow.collect { packet ->
                packet?.let {
                    // 转换为算法可用格式
                    val dataPoint = dataConverter.convert(it)

                    // 添加到算法引擎
                    if (_uiState.value.isTraining) {
                        swingEngine.processDataPoint(dataPoint)

                        // 可选：存储原始数据到本地
                        // storeSensorData(dataPoint)
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
     * 存储传感器数据（可选，用于后续分析）
     */
    private fun storeSensorData(dataPoint: com.smartform.tennis.algorithm.model.SensorDataPoint) {
        viewModelScope.launch {
            // 限制存储频率，避免数据库压力过大
            // 实际应用中可能需要批量存储
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
        super.onCleared()
        bluetoothManager.disconnect()
        swingEngine.reset()
    }
}
