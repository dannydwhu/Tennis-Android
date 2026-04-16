# Smartform Tennis - Android 应用

网球训练 Android 应用，通过蓝牙连接传感器设备，在端侧实现击球动作检测和识别。

## 项目结构

```
app/src/main/java/com/smartform/tennis/
├── TennisApplication.kt          # 应用入口
├── algorithm/                     # 核心算法模块
│   ├── TennisSwingEngine.kt       # 动作识别引擎（统一入口）
│   ├── DataPreprocessor.kt        # 数据预处理（滤波、去重力）
│   ├── SwingDetector.kt           # 动作检测器（状态机切割）
│   ├── SwingClassifier.kt         # 动作分类器（决策树）
│   ├── FeatureExtractor.kt        # 特征提取器
│   ├── SpeedCalculator.kt         # 速度计算器
│   └── model/
│       ├── SwingType.kt           # 击球类型枚举
│       ├── SensorDataPoint.kt     # 传感器数据点
│       ├── SwingEvent.kt          # 击球事件
│       └── DetectionState.kt      # 检测状态
├── bluetooth/                     # 蓝牙模块
│   ├── BluetoothManager.kt        # 蓝牙管理器
│   └── BluetoothDataConverter.kt  # 数据转换器
├── data/                          # 数据层
│   ├── local/                     # 本地存储（Room）
│   │   ├── AppDatabase.kt
│   │   ├── dao/                   # 数据访问对象
│   │   ├── entity/                # 实体类
│   │   └── DataSyncService.kt     # 数据同步服务
│   ├── model/                     # 数据模型
│   └── network/                   # 网络 API（后续开发）
└── ui/                            # UI 界面
    ├── MainActivity.kt            # 主页
    └── viewmodel/
        └── MainViewModel.kt       # 主页 ViewModel
```

## 核心算法流程

```
传感器数据流 (100Hz)
       ↓
[BluetoothManager] 接收蓝牙数据
       ↓
[BluetoothDataConverter] 转换为单位统一的数据
       ↓
[DataPreprocessor] 去重力、平滑滤波
       ↓
[SwingDetector] 状态机检测动作窗口
   ├─ IDLE → PRE_TRIGGER → TRIGGERED → RECOVERY
   └─ 输出：完整的动作数据段
       ↓
[FeatureExtractor] 提取特征
   ├─ 时域特征：均值、方差、峰值
   ├─ 波形特征：峰度、偏度、零交叉率
   └─ 旋转特征：方向、角度
       ↓
[SwingClassifier] 决策树分类
   ├─ 按 时长 → 截击/常规/发球
   ├─ 按 旋转方向 → 正手/反手
   ├─ 按 波形峰度 → 击球/切削
   └─ 输出：SwingType
       ↓
[SpeedCalculator] 速度计算
   ├─ 去除重力
   ├─ 梯形积分
   └─ 输出：km/h
       ↓
[SwingEvent] 存储并通知 UI
```

## 六类击球识别规则

| 击球类型 | 时长范围 | 旋转方向 | 波形特征 | 高度特征 |
|---------|---------|---------|---------|---------|
| 正手击球 | 300-600ms | 顺时针 | 尖锐脉冲 | 腰部 |
| 反手击球 | 300-600ms | 逆时针 | 尖锐脉冲 | 腰部 |
| 切削 | 400-600ms | 任意 | 平缓波形 | 多变 |
| 高压/发球 | >600ms | 顺时针 + 向上 | 强脉冲 | 头部以上 |
| 正手截击 | <300ms | 顺时针 | 短促脉冲 | 胸前 |
| 反手截击 | <300ms | 逆时针 | 短促脉冲 | 胸前 |

## 数据库设计

### Room 本地数据库

**sensor_raw_data** - 传感器原始数据
- sessionId, timestamp, sequenceNumber
- ax, ay, az, gx, gy, gz, mx, my, mz

**training_sessions** - 训练会话
- userId, startTime, endTime, duration
- totalShots, forehandCount, backhandCount, ...
- maxSpeed, avgSpeed, qualityScore

**shots** - 击球记录
- sessionId, userId, shotType
- maxSpeed, avgSpeed, timestamp
- qualityScore, confidenceScore, aiSuggestion

## 阈值参数（可调优）

```kotlin
// SwingDetector.kt
triggerThreshold = 12.0f        // 触发阈值 (m/s²)
staticMinThreshold = 8.0f       // 静止下限
staticMaxThreshold = 11.5f      // 静止上限
preTriggerDuration = 100L       // 预触发缓冲 (ms)
postTriggerDuration = 300L      // 动作后追加 (ms)

// SwingClassifier.kt
volleyDurationThreshold = 300L   // 截击时长上限
serveDurationThreshold = 600L    // 发球时长下限
kurtosisThreshold = 0.5f         // 峰度阈值
```

## 下一步开发

1. **完善 UI 界面**
   - 训练报告页面
   - 排行榜页面
   - 历史记录页面

2. **算法调优**
   - 收集实际数据进行参数校准
   - 引入机器学习模型（V2.0）

3. **后端对接**
   - 数据同步服务
   - 用户登录系统

4. **测试与优化**
   - 真机测试
   - 性能优化
   - 功耗优化

## 依赖库

- AndroidX Core, AppCompat, Material
- Room Database
- Kotlin Coroutines + Flow
- MPAndroidChart（图表）
- Retrofit + OkHttp（网络）

## 开发环境

- Android Studio Hedgehog+
- Kotlin 1.9.20
- Compile SDK 34
- Min SDK 21
