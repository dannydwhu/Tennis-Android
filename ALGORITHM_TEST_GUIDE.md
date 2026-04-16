# 算法调试指南

## 测试工具

### 1. 模拟数据生成器
位置：`MockSensorDataGenerator.kt`

生成 6 种击球类型的模拟传感器数据：

```kotlin
val generator = MockSensorDataGenerator()

// 生成单个击球数据
val forehandData = generator.generateForehand()    // 正手
val backhandData = generator.generateBackhand()    // 反手
val sliceData = generator.generateSlice()          // 切削
val serveData = generator.generateServe()          // 发球
val volleyFHData = generator.generateForehandVolley()  // 正手截击
val volleyBHData = generator.generateBackhandVolley()  // 反手截击

// 生成完整训练会话
val sessionData = generator.generateTrainingSession()
```

### 2. 算法测试器
位置：`SwingAlgorithmTester.kt`

运行算法测试并统计准确率：

```kotlin
val tester = SwingAlgorithmTester()

// 测试单个类型
val result = tester.testSingleSwingType(SwingType.FOREHAND) {
    generator.generateForehand()
}

// 测试完整会话
val sessionResult = tester.testTrainingSession()

// 运行全部测试
val overallResult = tester.runAllTests()
```

### 3. 算法调试器
位置：`AlgorithmDebugger.kt`

可视化和分析算法中间结果：

```kotlin
val debugger = AlgorithmDebugger()

// 打印数据统计
debugger.printDataStats(dataPoints, "正手击球数据")

// ASCII 波形图
debugger.printAccelerationChart(dataPoints)

// 打印分类决策过程
debugger.printClassificationProcess(features, result)
```

---

## 运行测试

### 方式 1：命令行运行
```bash
cd Tennis-Android
./gradlew run
```

### 方式 2：Android Studio 中运行
1. 打开 `AlgorithmTestRunner.kt`
2. 点击 main 函数旁边的运行按钮
3. 查看控制台输出

---

## 测试输出示例

```
╔════════════════════════════════════════════╗
║   Smartform Tennis 算法测试套件             ║
╚════════════════════════════════════════════╝

【第一部分】单动作识别测试
─────────────────────────────────────────

▶ 测试正手击球...
=== 测试：正手击球 ===
  动作 1: 识别为 正手击球 (置信度：0.85)
  动作 2: 识别为 正手击球 (置信度：0.82)

结果：正确 9/10, 准确率：90.0%
识别分布：正手击球=9, 切削=1

▶ 测试反手击球...
...

【第二部分】完整训练会话测试
─────────────────────────────────────────
检测到击球：正手击球，速度：105.3 km/h
检测到击球：反手击球，速度：98.2 km/h
...

【第三部分】测试报告
========================================
    Smartform Tennis 算法测试报告
========================================

各类型识别准确率:
  正手击球：[█████████░] 90.0%
  反手击球：[████████░░] 80.0%
  切削：[██████████] 100.0%
  发球/高压：[████████░░] 80.0%
  正手截击：[███████░░░] 70.0%
  反手截击：[█████████░] 90.0%

总体表现:
  总检测次数：60
  正确识别：52
  总体准确率：86.7%
```

---

## 调优参数

### SwingDetector.kt
```kotlin
triggerThreshold = 12.0f        // 触发阈值 (m/s²)
staticMinThreshold = 8.0f       // 静止下限
staticMaxThreshold = 11.5f      // 静止上限
preTriggerDuration = 100L       // 预触发缓冲 (ms)
postTriggerDuration = 300L      // 动作后追加 (ms)
```

### SwingClassifier.kt
```kotlin
volleyDurationThreshold = 300L   // 截击时长上限
serveDurationThreshold = 600L    // 发球时长下限
kurtosisThreshold = 0.5f         // 峰度阈值（区分击球/切削）
rotationAngleThreshold = 1.0f    // 旋转角度阈值
```

---

## 常见问题诊断

### 问题 1：检测到过多动作（误检）
**症状**：静止数据也被识别为动作

**解决**：
- 提高 `triggerThreshold`（如从 12.0 提高到 15.0）
- 提高 `angularVelocityThreshold`

### 问题 2：漏检动作
**症状**：实际击球未被检测到

**解决**：
- 降低 `triggerThreshold`（如从 12.0 降到 10.0）
- 降低 `staticMinThreshold`

### 问题 3：切削被识别为常规击球
**症状**：切削动作被识别为正手/反手

**解决**：
- 降低 `kurtosisThreshold`（如从 0.5 降到 0.3）
- 检查切削模拟数据的波形是否足够平缓

### 问题 4：截击识别不准确
**症状**：截击被识别为其他类型

**解决**：
- 调整 `volleyDurationThreshold`
- 检查截击数据的时长是否在 300ms 以内

---

## 添加新的测试用例

```kotlin
// 1. 在 MockSensorDataGenerator 添加新的生成方法
fun generateCustomSwing(): List<SensorDataPoint> {
    // ...生成自定义数据
}

// 2. 在 AlgorithmTestRunner 添加测试
tester.testSingleSwingType(SwingType.CUSTOM) {
    generator.generateCustomSwing()
}
```

---

## 性能基准

目标准确率：
- 正手击球：≥ 90%
- 反手击球：≥ 90%
- 切削：≥ 85%
- 发球/高压：≥ 85%
- 正手截击：≥ 80%
- 反手截击：≥ 80%
- **总体准确率：≥ 85%**
