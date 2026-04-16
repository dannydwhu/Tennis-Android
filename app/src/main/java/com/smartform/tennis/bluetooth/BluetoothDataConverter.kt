package com.smartform.tennis.bluetooth

import com.smartform.tennis.algorithm.model.SensorDataPoint

/**
 * 蓝牙数据转换器
 *
 * 将蓝牙接收的数据转换为算法模块可用的 SensorDataPoint
 */
class BluetoothDataConverter {

    // 数据序列号
    private var sequenceNumber: Long = 0

    /**
     * 转换蓝牙数据包为 SensorDataPoint
     *
     * @param packet 蓝牙接收的传感器数据包
     * @return SensorDataPoint
     */
    fun convert(packet: BluetoothManager.SensorDataPacket): SensorDataPoint {
        return SensorDataPoint(
            timestamp = packet.timestamp,
            // 加速度计：假设原始数据单位是 g，转换为 m/s²
            ax = packet.accelerometer.x * 9.81f,
            ay = packet.accelerometer.y * 9.81f,
            az = packet.accelerometer.z * 9.81f,
            // 陀螺仪：假设原始数据单位是 dps (度/秒)，转换为 rad/s
            gx = packet.gyroscope.x * (Math.PI / 180).toFloat(),
            gy = packet.gyroscope.y * (Math.PI / 180).toFloat(),
            gz = packet.gyroscope.z * (Math.PI / 180).toFloat(),
            // 磁力计：单位μT，直接使用
            mx = packet.magnetometer.x,
            my = packet.magnetometer.y,
            mz = packet.magnetometer.z
        )
    }

    /**
     * 批量转换
     */
    fun convertBatch(packets: List<BluetoothManager.SensorDataPacket>): List<SensorDataPoint> {
        return packets.mapNotNull { packet ->
            packet.let { convert(it) }
        }
    }

    /**
     * 重置序列号
     */
    fun resetSequence() {
        sequenceNumber = 0
    }

    /**
     * 获取并递增序列号
     */
    fun getNextSequenceNumber(): Long {
        return sequenceNumber++
    }
}
