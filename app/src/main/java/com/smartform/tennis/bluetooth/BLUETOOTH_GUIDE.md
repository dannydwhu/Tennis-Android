# 蓝牙模块使用指南

## 模块结构

```
bluetooth/
├── BluetoothManager.kt        # 核心蓝牙管理器
├── BluetoothDebugger.kt       # 调试工具
├── BluetoothConfig.kt         # 配置类
├── BluetoothDataConverter.kt  # 数据转换器
└── BLUETOOTH_GUIDE.md         # 本指南
```

---

## 快速开始

### 1. 基础使用

```kotlin
// 创建蓝牙管理器
val bluetoothManager = BluetoothManager(context)

// 监听连接状态
lifecycleScope.launch {
    bluetoothManager.connectionState.collect { state ->
        when (state) {
            BluetoothManager.BluetoothState.CONNECTED -> {
                // 已连接
            }
            BluetoothManager.BluetoothState.DISCONNECTED -> {
                // 已断开
            }
            else -> {}
        }
    }
}

// 监听传感器数据
lifecycleScope.launch {
    bluetoothManager.sensorDataFlow.collect { packet ->
        packet?.let {
            // 处理传感器数据
            val ax = it.accelerometer.x
            val gx = it.gyroscope.x
            // ...
        }
    }
}

// 扫描设备
bluetoothManager.startScan()

// 连接设备
bluetoothManager.connect(device)

// 断开连接
bluetoothManager.disconnect()
```

---

### 2. 使用配置

```kotlin
// 创建配置
val config = BluetoothConfig.Builder()
    .serviceUuid("0000180F-0000-1000-8000-00805F9B34FB")
    .notifyCharacteristicUuid("00002A19-0000-1000-8000-00805F9B34FB")
    .dataFormat(BluetoothConfig.DataFormat.FORMAT_9FLOAT)
    .autoReconnect(true)
    .build()

// 使用配置连接
// 注意：当前版本需要在连接前设置配置
```

---

### 3. 使用预设设备配置

```kotlin
// Arduino BLE 设备
bluetoothManager.connectWithProfile(BleDeviceProfiles.ARDUINO_BLE)

// ESP32 BLE 设备
bluetoothManager.connectWithProfile(BleDeviceProfiles.ESP32_BLE)

// MPU6050 加速度计
bluetoothManager.connectWithProfile(BleDeviceProfiles.MPU6050)
```

---

### 4. 使用调试工具

```kotlin
val debugger = BluetoothDebugger()

// 记录数据
bluetoothManager.sensorDataFlow.collect { packet ->
    packet?.let { debugger.logData(it) }
}

// 查看统计
debugger.printStats()

// 分析数据质量
val report = debugger.analyzeDataQuality()
println("数据质量：${report.getQualityLevel()}")
println("评分：${report.score}/100")

// 导出 CSV
val csv = debugger.exportToCsv()
```

---

## 数据格式说明

### FORMAT_9FLOAT (36 bytes)
```
偏移    类型    字段
0-3     float   加速度 X (ax)
4-7     float   加速度 Y (ay)
8-11    float   加速度 Z (az)
12-15   float   角速度 X (gx)
16-19   float   角速度 Y (gy)
20-23   float   角速度 Z (gz)
24-27   float   磁力计 X (mx)
28-31   float   磁力计 Y (my)
32-35   float   磁力计 Z (mz)
```

### FORMAT_6FLOAT (24 bytes)
```
偏移    类型    字段
0-3     float   加速度 X (ax)
4-7     float   加速度 Y (ay)
8-11    float   加速度 Z (az)
12-15   float   角速度 X (gx)
16-19   float   角速度 Y (gy)
20-23   float   角速度 Z (gz)
```

### FORMAT_12SHORT (24 bytes)
```
偏移    类型    字段
0-1     short   加速度 X
2-3     short   加速度 Y
4-5     short   加速度 Z
6-7     short   角速度 X
8-9     short   角速度 Y
10-11   short   角速度 Z
12-13   short   磁力计 X
14-15   short   磁力计 Y
16-17   short   磁力计 Z
18-19   short   保留
20-23   short   保留
```

---

## 完整使用示例

### 在 ViewModel 中使用

```kotlin
class TrainingViewModel @Inject constructor(
    private val application: Application
) : AndroidViewModel(application) {

    private val bluetoothManager = BluetoothManager(application)
    private val dataConverter = BluetoothDataConverter()
    private val swingEngine = TennisSwingEngine()
    private val debugger = BluetoothDebugger()

    // UI 状态
    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    init {
        // 监听蓝牙数据
        viewModelScope.launch {
            bluetoothManager.sensorDataFlow.collect { packet ->
                packet?.let {
                    // 记录调试数据
                    debugger.logData(it)

                    // 转换为算法可用格式
                    val dataPoint = dataConverter.convert(it)

                    // 如果正在训练，处理数据
                    if (_uiState.value.isTraining) {
                        swingEngine.processDataPoint(dataPoint)
                    }
                }
            }
        }

        // 监听连接状态
        viewModelScope.launch {
            bluetoothManager.connectionState.collect { state ->
                _uiState.update {
                    it.copy(
                        isConnected = state == BluetoothManager.BluetoothState.CONNECTED,
                        isConnecting = state == BluetoothManager.BluetoothState.CONNECTING
                    )
                }
            }
        }
    }

    fun scanDevices() {
        bluetoothManager.startScan()
    }

    fun connectDevice(device: BluetoothDevice) {
        bluetoothManager.connect(device)
    }

    fun disconnectDevice() {
        bluetoothManager.disconnect()
    }

    fun startTraining() {
        swingEngine.startSession(
            sessionId = System.currentTimeMillis(),
            userId = 1  // 从登录状态获取
        )
        _uiState.update { it.copy(isTraining = true) }
    }

    fun stopTraining() {
        val stats = swingEngine.endSession()
        _uiState.update {
            it.copy(
                isTraining = false,
                sessionStats = stats
            )
        }
    }

    fun getDebugReport(): String {
        val quality = debugger.analyzeDataQuality()
        return buildString {
            appendLine("数据质量报告")
            appendLine("评分：${quality.score}/100")
            appendLine("等级：${quality.getQualityLevel()}")
            appendLine("平均间隔：${quality.averageIntervalMs}ms")
            appendLine("稳定性：${quality.intervalStabilityMs}ms")
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothManager.destroy()
        swingEngine.reset()
    }
}

data class TrainingUiState(
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isTraining: Boolean = false,
    val sessionStats: EngineStats? = null
)
```

---

## 权限配置

### AndroidManifest.xml

```xml
<!-- 蓝牙权限 (Android 12+) -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN"
    android:usesPermissionFlags="neverForLocation"
    tools:targetApi="s" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

<!-- 蓝牙权限 (Android 11 及以下) -->
<uses-permission android:name="android.permission.BLUETOOTH"
    android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"
    android:maxSdkVersion="30" />

<!-- 位置权限 (用于蓝牙扫描) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- 蓝牙特性声明 -->
<uses-feature android:name="android.hardware.bluetooth" android:required="true" />
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true" />
```

### 运行时权限请求

```kotlin
private val permissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { permissions ->
    val allGranted = permissions.values.all { it }
    if (allGranted) {
        // 开始扫描
        bluetoothManager.startScan()
    } else {
        Toast.makeText(this, "需要蓝牙权限", Toast.LENGTH_SHORT).show()
    }
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
    }
}
```

---

## 常见问题

### Q1: 如何配置硬件 UUID？

在 `BluetoothConfig.kt` 中修改：

```kotlin
val config = BluetoothConfig.Builder()
    .serviceUuid("你的服务 UUID")
    .notifyCharacteristicUuid("你的特征 UUID")
    .build()
```

### Q2: 数据格式不匹配怎么办？

根据硬件协议修改 `BluetoothManager.parseSensorData()` 方法：

```kotlin
private fun parseSensorData(data: ByteArray) {
    // 根据你的协议解析
    // 例如：如果是自定义协议，添加新的解析方法
    parseCustomProtocol(data)
}
```

### Q3: 如何添加自动重连？

在配置中启用：

```kotlin
val config = BluetoothConfig.Builder()
    .autoReconnect(true)
    .reconnectIntervalMs(3000L)  // 3 秒后重连
    .maxReconnectAttempts(3)
    .build()
```

### Q4: 数据接收不稳定？

使用调试工具分析：

```kotlin
val report = debugger.analyzeDataQuality()
if (report.score < 70) {
    // 数据质量不佳，检查：
    // 1. 蓝牙连接距离
    // 2. 电磁干扰
    // 3. 硬件固件
}
```

---

## 调试技巧

### 1. 启用详细日志

```kotlin
// 在 Application 中
if (BuildConfig.DEBUG) {
    BluetoothManager.enableVerboseLogging(true)
}
```

### 2. 监控数据流

```kotlin
// 在 Activity/Fragment 中
bluetoothManager.sensorDataFlow
    .onEach { packet ->
        Log.d("BT_DATA", "收到数据：${packet?.timestamp}")
    }
    .launchIn(lifecycleScope)
```

### 3. 抓取问题数据

```kotlin
// 保存异常数据用于分析
bluetoothManager.sensorDataFlow.collect { packet ->
    if (packet?.accelerationMagnitude ?: 0f > 50f) {
        // 异常数据，保存到文件
        saveAnomalyData(packet)
    }
}
```

---

## 与算法模块对接

```kotlin
// 完整数据流
传感器硬件
    ↓ BLE
BluetoothManager
    ↓ SensorDataPacket
BluetoothDataConverter
    ↓ SensorDataPoint
DataPreprocessor (去重力、滤波)
    ↓
SwingDetector (动作检测)
    ↓
SwingClassifier (动作识别)
    ↓
SpeedCalculator (速度计算)
    ↓
SwingEvent (输出结果)
```

---

## 下一步

1. **确认硬件协议** - 与硬件团队确认 UUID 和数据格式
2. **真机测试** - 连接实际传感器测试
3. **参数调优** - 根据实际数据调整解析逻辑
