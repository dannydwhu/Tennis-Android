package com.smartform.tennis.bluetooth

import android.util.Log
import com.smartform.tennis.bluetooth.BluetoothManager.SensorDataPacket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 蓝牙调试工具
 *
 * 用于调试蓝牙连接和数据接收
 */
class BluetoothDebugger {

    companion object {
        private const val TAG = "BluetoothDebugger"
    }

    // 数据统计
    private val _statsFlow = MutableStateFlow(BluetoothStats())
    val statsFlow: StateFlow<BluetoothStats> = _statsFlow.asStateFlow()

    // 数据日志
    private val dataLog = mutableListOf<DataLogEntry>()
    private val maxLogSize = 100

    /**
     * 记录接收到的数据
     */
    fun logData(packet: SensorDataPacket) {
        val entry = DataLogEntry(
            timestamp = packet.timestamp,
            accelMagnitude = packet.accelerationMagnitude,
            gyroMagnitude = packet.gyroMagnitude,
            rawData = packet
        )

        dataLog.add(entry)

        // 限制日志大小
        if (dataLog.size > maxLogSize) {
            dataLog.removeAt(0)
        }

        // 更新统计
        val current = _statsFlow.value
        _statsFlow.value = current.copy(
            totalPackets = current.totalPackets + 1,
            lastPacketTime = packet.timestamp,
            avgAccelMagnitude = (current.avgAccelMagnitude * current.totalPackets + packet.accelerationMagnitude) /
                    (current.totalPackets + 1),
            avgGyroMagnitude = (current.avgGyroMagnitude * current.totalPackets + packet.gyroMagnitude) /
                    (current.totalPackets + 1)
        )

        // 详细日志
        Log.d(TAG, "数据包 #${current.totalPackets}: " +
                "加速度=${String.format("%.2f", packet.accelerationMagnitude)} m/s², " +
                "角速度=${String.format("%.2f", packet.gyroMagnitude)} rad/s")
    }

    /**
     * 打印数据统计
     */
    fun printStats() {
        val stats = _statsFlow.value
        Log.i(TAG, "========== 蓝牙数据统计 ==========")
        Log.i(TAG, "总数据包数：${stats.totalPackets}")
        Log.i(TAG, "最后数据包时间：${stats.lastPacketTime}")
        Log.i(TAG, "平均加速度：${String.format("%.2f", stats.avgAccelMagnitude)} m/s²")
        Log.i(TAG, "平均角速度：${String.format("%.2f", stats.avgGyroMagnitude)} rad/s")
        Log.i(TAG, "================================")
    }

    /**
     * 获取数据日志
     */
    fun getDataLog(): List<DataLogEntry> {
        return dataLog.toList()
    }

    /**
     * 清除日志
     */
    fun clearLog() {
        dataLog.clear()
        _statsFlow.value = BluetoothStats()
    }

    /**
     * 分析数据质量
     */
    fun analyzeDataQuality(): DataQualityReport {
        if (dataLog.isEmpty()) {
            return DataQualityReport.INVALID
        }

        // 检查数据间隔
        val intervals = mutableListOf<Long>()
        for (i in 1 until dataLog.size) {
            intervals.add(dataLog[i].timestamp - dataLog[i - 1].timestamp)
        }

        val avgInterval = intervals.average()
        val intervalStdDev = if (intervals.size > 1) {
            sqrt(intervals.map { (it - avgInterval).pow(2) }.average())
        } else {
            0.0
        }

        // 检查数据范围
        val accelValues = dataLog.map { it.accelMagnitude }
        val gyroValues = dataLog.map { it.gyroMagnitude }

        val hasReasonableAccel = accelValues.any { it in 5.0..25.0 }  // 0.5g - 2.5g
        val hasReasonableGyro = gyroValues.any { it in 0.1..10.0 }    // 合理角速度范围

        // 评估质量
        var score = 100

        // 间隔稳定性（100Hz 应该是 10ms）
        if (avgInterval < 8 || avgInterval > 12) {
            score -= 20
        }
        if (intervalStdDev > 5) {
            score -= 20
        }

        // 数据范围合理性
        if (!hasReasonableAccel) score -= 20
        if (!hasReasonableGyro) score -= 20

        return DataQualityReport(
            score = score,
            averageIntervalMs = avgInterval,
            intervalStabilityMs = intervalStdDev,
            hasReasonableAccel = hasReasonableAccel,
            hasReasonableGyro = hasReasonableGyro,
            dataPoints = dataLog.size
        )
    }

    /**
     * 导出数据为 CSV
     */
    fun exportToCsv(): String {
        val sb = StringBuilder()
        sb.appendLine("timestamp,ax,ay,az,gx,gy,gz,mx,my,mz,accel_mag,gyro_mag")

        for (entry in dataLog) {
            val p = entry.rawData
            sb.appendLine(
                "${p.timestamp}," +
                "${p.accelerometer.x}," +
                "${p.accelerometer.y}," +
                "${p.accelerometer.z}," +
                "${p.gyroscope.x}," +
                "${p.gyroscope.y}," +
                "${p.gyroscope.z}," +
                "${p.magnetometer.x}," +
                "${p.magnetometer.y}," +
                "${p.magnetometer.z}," +
                "${p.accelerationMagnitude}," +
                "${p.gyroMagnitude}"
            )
        }

        return sb.toString()
    }
}

/**
 * 蓝牙统计信息
 */
data class BluetoothStats(
    val totalPackets: Int = 0,
    val lastPacketTime: Long = 0,
    val avgAccelMagnitude: Float = 0f,
    val avgGyroMagnitude: Float = 0f
)

/**
 * 数据日志条目
 */
data class DataLogEntry(
    val timestamp: Long,
    val accelMagnitude: Float,
    val gyroMagnitude: Float,
    val rawData: SensorDataPacket
)

/**
 * 数据质量报告
 */
data class DataQualityReport(
    val score: Int,          // 0-100
    val averageIntervalMs: Double,
    val intervalStabilityMs: Double,
    val hasReasonableAccel: Boolean,
    val hasReasonableGyro: Boolean,
    val dataPoints: Int
) {
    companion object {
        val INVALID = DataQualityReport(
            score = 0,
            averageIntervalMs = 0.0,
            intervalStabilityMs = 0.0,
            hasReasonableAccel = false,
            hasReasonableGyro = false,
            dataPoints = 0
        )
    }

    fun getQualityLevel(): String {
        return when {
            score >= 90 -> "优秀"
            score >= 70 -> "良好"
            score >= 50 -> "合格"
            else -> "需要校准"
        }
    }
}

/**
 * SensorDataPacket 扩展属性
 */
val SensorDataPacket.accelerationMagnitude: Float
    get() = kotlin.math.sqrt(
        accelerometer.x * accelerometer.x +
        accelerometer.y * accelerometer.y +
        accelerometer.z * accelerometer.z
    )

val SensorDataPacket.gyroMagnitude: Float
    get() = kotlin.math.sqrt(
        gyroscope.x * gyroscope.x +
        gyroscope.y * gyroscope.y +
        gyroscope.z * gyroscope.z
    )
