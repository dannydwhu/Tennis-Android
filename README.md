# Smartform Tennis - Android 应用

网球训练 Android 应用，通过蓝牙连接传感器设备，在端侧实现击球动作检测和识别。

## 品牌定位

- **产品名称**：Smartform（智塑科技 · 网球运动传感器配套 App）
- **品牌调性**：专业、科技、精准、活力
- **设计原则**：数据优先、沉浸体验（深色主题）、专业反馈、动感交互

---

## UI 设计规范

### 色彩体系

#### 品牌主色
| 用途 | 色值 | 说明 |
| :--- | :--- | :--- |
| 品牌主色 | `#00D68F` | 品牌绿，代表运动、活力、精准感 |
| 品牌辅色 | `#3366FF` | 科技蓝，用于次要操作和数据高亮 |
| 强调色 | `#FF6B35` | 爆发橙，用于速度峰值、成就、警示 |
| 深色背景 | `#0D1117` | 主背景色，降低训练时屏幕干扰 |
| 卡片背景 | `#161B22` | 卡片/模块背景 |

#### 六项击球动作语义色
| 数据类型 | 色值 | 用途 |
| :--- | :--- | :--- |
| 正手击球 | `#00D68F` | 品牌绿 |
| 反手击球 | `#3366FF` | 科技蓝 |
| 切削球 | `#FFD600` | 明黄 |
| 高压/发球 | `#FF6B35` | 爆发橙 |
| 正手截击 | `#9B51E0` | 紫色 |
| 反手截击 | `#00BCD4` | 青色 |
| 速度峰值 | `#FF1744` | 红色（最高速度标记） |

#### 文字层级色值
| 层级 | 色值 | 用途 |
| :--- | :--- | :--- |
| 主文字 | `#FFFFFF` / `#FFFFFFCC` | 标题、重要数据 |
| 次要文字 | `#FFFFFF99` | 副标题、说明文字 |
| 辅助文字 | `#FFFFFF66` | 标签、时间戳 |
| 禁用/弱化 | `#FFFFFF33` | 不可操作状态 |

### 字体规范

| 用途 | 字号 | 字重 | 行高 |
| :--- | :--- | :--- | :--- |
| 大屏数据（速度/个数） | 64px | Bold (700) | 1.0 |
| 页面标题 | 28px | Semibold (600) | 1.2 |
| 卡片标题 | 20px | Semibold (600) | 1.3 |
| 正文数据 | 18px | Medium (500) | 1.4 |
| 正文内容 | 16px | Regular (400) | 1.5 |
| 辅助说明 | 14px | Regular (400) | 1.4 |
| 标签/角标 | 12px | Medium (500) | 1.2 |

### 间距系统

- **基础单位**：4px
- **常用倍数**：x4 / x8 / x12 / x16 / x20 / x24 / x32 / x48

| 位置 | 间距 |
| :--- | :--- |
| 页面左右边距 | 20px (5x) |
| 卡片内边距 | 16px (4x) |
| 卡片间距 | 12px (3x) |
| 列表项间距 | 8px (2x) |

### 圆角系统

| 组件 | 圆角 |
| :--- | :--- |
| 大卡片（训练报告） | 20px |
| 小卡片/按钮 | 12px |
| 标签/角标 | 8px |
| 头像 | 50%（圆形） |
| 图标容器 | 12px |

---

## 功能模块

### 底部导航栏（全局）
- **Tab 项**：LIVE（实况）、数据、分析、排行榜、我的
- **样式**：深色背景 `#0D1117`，选中态品牌绿 `#00D68F`，未选中态辅助文字 `#FFFFFF66`
- **高度**：48px + 安全区域

### 实况页（LIVE）
- **开始训练按钮**：品牌绿渐变，检测蓝牙连接后进入训练状态
- **六项技术动作卡片**：2x3 网格，实时显示击球个数
- **最大挥速仪表盘**：64px Bold 数字，速度峰值时变红色
- **连击计数器**：连续有效击球次数
- **训练时长/卡路里**：实时计时和估算

### 数据页（DATA）
- **时间切换 Tab**：今日、本周、本月、本年
- **核心指标卡片**：击球数、卡路里、运动时长、最大挥速
- **能力分析五维图**：爆发力、进攻性、活跃度、对抗性、耐力
- **挥速区间分布**：直方图展示

### 分析页（ANALYSIS）
- **可用次数展示**：视频分析剩余次数
- **分析类型入口**：发球分析、正手分析、反手分析

### 排行榜页（RANKING）
- **榜单类型 Tab**：击球数榜、时长榜、速度榜、总榜
- **榜单列表**：Top 10 用户，含头像、昵称、数据、称号
- **本人排名卡片**：固定在底部高亮显示

### 我的页面（PROFILE）
- **用户信息头部**：头像、昵称、ID、等级
- **成长系统进度**：经验进度条
- **历史记录列表**：按时间倒序展示训练摘要
- **设置入口**：设备管理、账号安全、法律条款等

---

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
