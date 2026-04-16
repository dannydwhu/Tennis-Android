package com.smartform.tennis.bluetooth

import java.util.UUID

/**
 * 蓝牙配置
 *
 * 配置蓝牙服务的 UUID 和数据格式
 */
data class BluetoothConfig(
    // 服务 UUID
    val serviceUuid: UUID = BluetoothManager.DEFAULT_SENSOR_SERVICE_UUID,

    // 通知特征 UUID
    val notifyCharacteristicUuid: UUID = BluetoothManager.DEFAULT_SENSOR_CHARACTERISTIC_UUID,

    // 写入特征 UUID（可选）
    val writeCharacteristicUuid: UUID? = null,

    // 数据格式
    val dataFormat: DataFormat = DataFormat.FORMAT_9FLOAT,

    // 是否启用自动重连
    val autoReconnect: Boolean = true,

    // 重连间隔（毫秒）
    val reconnectIntervalMs: Long = 3000L,

    // 最大重连次数
    val maxReconnectAttempts: Int = 3,

    // 数据超时（毫秒），超过此时间未收到数据认为断开
    val dataTimeoutMs: Long = 5000L
) {
    /**
     * 数据格式枚举
     */
    enum class DataFormat(val packetSize: Int) {
        FORMAT_9FLOAT(36),   // 9 个 float: ax,ay,az,gx,gy,gz,mx,my,mz
        FORMAT_6FLOAT(24),   // 6 个 float: ax,ay,az,gx,gy,gz
        FORMAT_12SHORT(24),  // 12 个 short: 原始数据
        CUSTOM(0)            // 自定义格式
    }

    /**
     * 构建器
     */
    class Builder {
        private var serviceUuid: UUID = BluetoothManager.DEFAULT_SENSOR_SERVICE_UUID
        private var notifyCharUuid: UUID = BluetoothManager.DEFAULT_SENSOR_CHARACTERISTIC_UUID
        private var writeCharUuid: UUID? = null
        private var dataFormat: DataFormat = DataFormat.FORMAT_9FLOAT
        private var autoReconnect: Boolean = true
        private var reconnectIntervalMs: Long = 3000L
        private var maxReconnectAttempts: Int = 3
        private var dataTimeoutMs: Long = 5000L

        fun serviceUuid(uuid: UUID) = apply { this.serviceUuid = uuid }
        fun serviceUuid(uuid: String) = apply { this.serviceUuid = UUID.fromString(uuid) }

        fun notifyCharacteristicUuid(uuid: UUID) = apply { this.notifyCharUuid = uuid }
        fun notifyCharacteristicUuid(uuid: String) = apply { this.notifyCharUuid = UUID.fromString(uuid) }

        fun writeCharacteristicUuid(uuid: UUID?) = apply { this.writeCharUuid = uuid }
        fun writeCharacteristicUuid(uuid: String?) = apply {
            this.writeCharUuid = uuid?.let { UUID.fromString(it) }
        }

        fun dataFormat(format: DataFormat) = apply { this.dataFormat = format }

        fun autoReconnect(enabled: Boolean) = apply { this.autoReconnect = enabled }
        fun reconnectInterval(ms: Long) = apply { this.reconnectIntervalMs = ms }
        fun maxReconnectAttempts(count: Int) = apply { this.maxReconnectAttempts = count }
        fun dataTimeout(ms: Long) = apply { this.dataTimeoutMs = ms }

        fun build(): BluetoothConfig {
            return BluetoothConfig(
                serviceUuid = serviceUuid,
                notifyCharacteristicUuid = notifyCharUuid,
                writeCharacteristicUuid = writeCharUuid,
                dataFormat = dataFormat,
                autoReconnect = autoReconnect,
                reconnectIntervalMs = reconnectIntervalMs,
                maxReconnectAttempts = maxReconnectAttempts,
                dataTimeoutMs = dataTimeoutMs
            )
        }
    }
}

/**
 * 常用 BLE 设备配置
 */
object BleDeviceProfiles {

    /**
     * Nordic nRF52 系列
     */
    val NORDIC_NRF52 = BluetoothConfig.Builder()
        .serviceUuid("0000180D-0000-1000-8000-00805F9B34FB")  // Heart Rate Service
        .notifyCharacteristicUuid("00002A37-0000-1000-8000-00805F9B34FB")
        .dataFormat(BluetoothConfig.DataFormat.FORMAT_6FLOAT)
        .build()

    /**
     * Arduino BLE
     */
    val ARDUINO_BLE = BluetoothConfig.Builder()
        .serviceUuid("19B10000-E8F2-537E-4F6C-D104768A1214")
        .notifyCharacteristicUuid("19B10001-E8F2-537E-4F6C-D104768A1214")
        .writeCharacteristicUuid("19B10002-E8F2-537E-4F6C-D104768A1214")
        .dataFormat(BluetoothConfig.DataFormat.FORMAT_9FLOAT)
        .build()

    /**
     * ESP32 BLE
     */
    val ESP32_BLE = BluetoothConfig.Builder()
        .serviceUuid("4C2B1234-E8F2-537E-4F6C-D104768A1214")
        .notifyCharacteristicUuid("4C2B1235-E8F2-537E-4F6C-D104768A1214")
        .dataFormat(BluetoothConfig.DataFormat.FORMAT_9FLOAT)
        .build()

    /**
     * 通用加速度计（如 MPU6050）
     */
    val MPU6050 = BluetoothConfig.Builder()
        .serviceUuid(BluetoothManager.DEFAULT_SENSOR_SERVICE_UUID)
        .notifyCharacteristicUuid(BluetoothManager.DEFAULT_SENSOR_CHARACTERISTIC_UUID)
        .dataFormat(BluetoothConfig.DataFormat.FORMAT_6FLOAT)  // 只有加速度和陀螺仪
        .build()
}
