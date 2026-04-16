# Smartform Tennis - 快速开始指南

## 项目位置
```
/Users/hudawei/j2ee/workspace/Tennis-Android/
```

## 核心功能速览

### 1. 动作检测算法
位置：`algorithm/`

```kotlin
// 使用示例
val engine = TennisSwingEngine()

// 开始训练会话
engine.startSession(sessionId, userId)

// 处理传感器数据
val swingEvent = engine.processDataPoint(sensorDataPoint)

// 获取统计
val stats = engine.getCurrentStats()

// 结束训练
val finalStats = engine.endSession()
```

### 2. 蓝牙连接
位置：`bluetooth/`

```kotlin
val bluetoothManager = BluetoothManager(context)

// 开始扫描
bluetoothManager.startScan()

// 连接设备
bluetoothManager.connect(device)

// 监听数据流
lifecycleScope.launch {
    bluetoothManager.sensorDataFlow.collect { packet ->
        // 处理传感器数据
    }
}
```

### 3. 数据存储
位置：`data/local/`

```kotlin
val database = AppDatabase.getInstance(context)

// 插入训练会话
database.trainingSessionDao().insert(session)

// 插入击球记录
database.shotDao().insert(shot)

// 查询历史
val sessions = database.trainingSessionDao().getByUserId(userId)
```

---

## 阈值参数调优

位于各算法类中，关键参数：

```kotlin
// SwingDetector.kt
triggerThreshold = 12.0f        // 动作触发阈值 (m/s²)
staticMinThreshold = 8.0f       // 静止下限
staticMaxThreshold = 11.5f      // 静止上限

// SwingClassifier.kt
volleyDurationThreshold = 300L   // 截击时长上限 (ms)
serveDurationThreshold = 600L    // 发球时长下限 (ms)
kurtosisThreshold = 0.5f         // 峰度阈值
```

---

## 硬件协议对接

需要硬件团队提供：

1. **蓝牙服务 UUID**
2. **数据特征 UUID**
3. **数据包格式**（目前假设 36 字节浮点数组）

当前解析代码位于 `BluetoothManager.parseSensorData()`：
```kotlin
// 假设格式：9 个 float (4 bytes each) = 36 bytes
// [0-3]   ax
// [4-7]   ay
// [8-11]  az
// [12-15] gx
// [16-19] gy
// [20-23] gz
// [24-27] mx
// [28-31] my
// [32-35] mz
```

---

## 数据库字段说明

### TrainingSessionEntity
| 字段 | 说明 |
|------|------|
| forehandCount | 正手击球数 |
| backhandCount | 反手击球数 |
| sliceCount | 切削数 |
| serveCount | 发球/高压数 |
| forehandVolleyCount | 正手截击数 |
| backhandVolleyCount | 反手截击数 |
| maxSpeed | 最大速度 (km/h) |

### ShotEntity
| 字段 | 说明 |
|------|------|
| shotType | 击球类型枚举字符串 |
| maxSpeed | 该次击球最大速度 |
| qualityScore | 质量评分 (0-100) |
| confidenceScore | 识别置信度 (0-1) |
| aiSuggestion | AI 建议文本 |

---

## 常见问题

### Q: 如何调整识别灵敏度？
A: 修改 `SwingDetector.kt` 中的 `triggerThreshold` 值
- 调低（如 10.0）→ 更容易触发动作检测
- 调高（如 15.0）→ 需要更大幅度才触发

### Q: 如何校准速度？
A: 使用 `SpeedCalculator.calibrate()` 方法
```kotlin
val calibrationFactor = speedCalculator.calibrate(
    standardSpeedKmh = 100f,  // 已知标准速度
    dataWindow = dataWindow
)
```

### Q: 如何添加新的击球类型？
A: 在 `SwingType.kt` 添加枚举，然后在 `SwingClassifier.kt` 添加分类规则

---

## 测试建议

1. **单元测试** - 测试算法各模块
2. **真机测试** - 连接传感器实地测试
3. **数据收集** - 记录实际击球数据用于调优
4. **性能测试** - 关注功耗和发热

---

## 联系协作

- **硬件对接**: 需要蓝牙协议文档
- **算法优化**: 需要实际击球数据集
- **后端开发**: 需要 API 接口定义
