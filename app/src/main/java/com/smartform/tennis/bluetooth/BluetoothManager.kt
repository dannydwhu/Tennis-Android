package com.smartform.tennis.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 蓝牙管理器 (增强版)
 *
 * 负责扫描、连接 BLE 设备，接收传感器数据
 *
 * 功能：
 * - BLE 设备扫描（支持过滤）
 * - GATT 连接管理
 * - 特征值通知/读取/写入
 * - 传感器数据解析（支持多种数据格式）
 * - 自动重连
 */
@SuppressLint("MissingPermission")
class BluetoothManager(private val context: Context) {

    companion object {
        private const val TAG = "BluetoothManager"

        // 默认的传感器服务 UUID（需要根据硬件修改）
        val DEFAULT_SENSOR_SERVICE_UUID: java.util.UUID =
            java.util.UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB")

        // 默认的传感器特征 UUID（需要根据硬件修改）
        val DEFAULT_SENSOR_CHARACTERISTIC_UUID: java.util.UUID =
            java.util.UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB")
    }

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getBtAdapter()

    private var bluetoothGatt: BluetoothGatt? = null
    private var notifyCharacteristic: BluetoothGattCharacteristic? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null

    // 连接状态
    private val _connectionState = MutableStateFlow(BluetoothState.DISCONNECTED)
    val connectionState: StateFlow<BluetoothState> = _connectionState.asStateFlow()

    // 接收到的数据
    private val _sensorDataFlow = MutableStateFlow<SensorDataPacket?>(null)
    val sensorDataFlow: StateFlow<SensorDataPacket?> = _sensorDataFlow.asStateFlow()

    // 扫描结果
    private val _scanResults = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults: StateFlow<List<ScanResult>> = _scanResults.asStateFlow()

    // 错误信息
    private val _errorFlow = MutableStateFlow<BluetoothError?>(null)
    val errorFlow: StateFlow<BluetoothError?> = _errorFlow.asStateFlow()

    // 扫描回调
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d(TAG, "扫描到设备：${result.device.name ?: result.device.address}")
            val current = _scanResults.value.toMutableList()
            if (result.device !in current.map { it.device }) {
                current.add(result)
                _scanResults.value = current
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            _scanResults.value = results
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "扫描失败：$errorCode")
            _scanResults.value = emptyList()
            _errorFlow.value = BluetoothError.ScanFailed(errorCode)
        }
    }

    // GATT 回调
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Log.d(TAG, "连接状态变化：${connectionStateToString(newState)}, status=$status")

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = BluetoothState.CONNECTED
                    bluetoothGatt = gatt
                    _errorFlow.value = null
                    // 发现服务
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = BluetoothState.DISCONNECTED
                    bluetoothGatt = null
                    notifyCharacteristic = null
                    writeCharacteristic = null
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "服务发现成功")
                // 查找传感器特征
                findSensorCharacteristics(gatt)
            } else {
                Log.e(TAG, "服务发现失败：$status")
                _errorFlow.value = BluetoothError.ServiceDiscoveryFailed(status)
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                parseSensorData(value)
            } else {
                Log.e(TAG, "读取特征失败：$status")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            // 接收传感器数据通知
            Log.d(TAG, "收到数据通知：${value.size} bytes")
            parseSensorData(value)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "写入成功：${characteristic.uuid}")
            } else {
                Log.e(TAG, "写入失败：$status")
                _errorFlow.value = BluetoothError.WriteFailed(status)
            }
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            Log.d(TAG, "描述符读取：${descriptor.uuid}, status=$status")
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "描述符写入成功")
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "MTU 变更为：$mtu")
            }
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "信号强度：$rssi dBm")
            }
        }
    }

    // ==================== 扫描功能 ====================

    /**
     * 扫描 BLE 设备
     */
    fun startScan() {
        Log.d(TAG, "开始扫描 BLE 设备")
        _scanResults.value = emptyList()
        _connectionState.value = BluetoothState.SCANNING
        val scanner = bluetoothAdapter?.bluetoothLeScanner ?: return
        scanner.startScan(scanCallback)
    }

    /**
     * 扫描指定名称的设备（支持模糊匹配）
     */
    fun startScanWithNameFilter(namePattern: String) {
        Log.d(TAG, "开始扫描，名称过滤：$namePattern")
        val scanner = bluetoothAdapter?.bluetoothLeScanner ?: return
        val filter = ScanFilter.Builder()
            .setDeviceName(namePattern)
            .build()
        scanner.startScan(listOf(filter), scanCallback)
    }

    /**
     * 扫描指定服务的设备
     */
    fun startScanWithServiceFilter(serviceUuid: java.util.UUID) {
        Log.d(TAG, "开始扫描，服务过滤：$serviceUuid")
        val scanner = bluetoothAdapter?.bluetoothLeScanner ?: return
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(serviceUuid))
            .build()
        scanner.startScan(listOf(filter), scanCallback)
    }

    /**
     * 停止扫描
     */
    fun stopScan() {
        Log.d(TAG, "停止扫描")
        val scanner = bluetoothAdapter?.bluetoothLeScanner ?: return
        scanner.stopScan(scanCallback)
        if (_connectionState.value != BluetoothState.CONNECTED) {
            _connectionState.value = BluetoothState.DISCONNECTED
        }
    }

    // ==================== 连接功能 ====================

    /**
     * 连接设备
     */
    fun connect(device: BluetoothDevice, autoConnect: Boolean = false) {
        Log.d(TAG, "连接设备：${device.name ?: device.address}, autoConnect=$autoConnect")
        _connectionState.value = BluetoothState.CONNECTING

        val gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.connectGatt(context, autoConnect, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.connectGatt(context, autoConnect, gattCallback)
        }

        bluetoothGatt = gatt
    }

    /**
     * 按地址连接设备
     */
    fun connectByAddress(address: String) {
        Log.d(TAG, "按地址连接设备：$address")
        val device = bluetoothAdapter?.getRemoteDevice(address) ?: run {
            Log.e(TAG, "设备地址无效：$address")
            _errorFlow.value = BluetoothError.InvalidDeviceAddress
            return
        }
        connect(device)
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        Log.d(TAG, "断开连接")
        bluetoothGatt?.let { gatt ->
            // 取消通知
            notifyCharacteristic?.let {
                gatt.setCharacteristicNotification(it, false)
            }
            gatt.disconnect()
            gatt.close()
        }
        bluetoothGatt = null
        notifyCharacteristic = null
        writeCharacteristic = null
        _connectionState.value = BluetoothState.DISCONNECTED
    }

    // ==================== 数据操作 ====================

    /**
     * 读取特征值
     */
    fun readCharacteristic(uuid: java.util.UUID): Boolean {
        return bluetoothGatt?.let { gatt ->
            gatt.getService(DEFAULT_SENSOR_SERVICE_UUID)?.getCharacteristic(uuid)?.let {
                gatt.readCharacteristic(it)
                true
            } ?: false
        } ?: false
    }

    /**
     * 写入特征值
     */
    fun writeCharacteristic(uuid: java.util.UUID, value: ByteArray): Boolean {
        return bluetoothGatt?.let { gatt ->
            gatt.getService(DEFAULT_SENSOR_SERVICE_UUID)?.getCharacteristic(uuid)?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeCharacteristic(it, value, it.writeType)
                } else {
                    @Suppress("DEPRECATION")
                    it.value = value
                    gatt.writeCharacteristic(it)
                }
                true
            } ?: false
        } ?: false
    }

    /**
     * 请求 MTU 大小
     */
    fun requestMtu(mtu: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothGatt?.requestMtu(mtu)
        }
    }

    /**
     * 读取信号强度
     */
    fun readRemoteRssi() {
        bluetoothGatt?.readRemoteRssi()
    }

    // ==================== 私有方法 ====================

    /**
     * 查找传感器特征
     */
    private fun findSensorCharacteristics(gatt: BluetoothGatt?) {
        gatt?.let {
            // 查找传感器服务
            val sensorService = it.getService(DEFAULT_SENSOR_SERVICE_UUID)
                ?: gatt.services.find { service ->
                    service.uuid.toString().contains("180f", ignoreCase = true)
                }

            if (sensorService != null) {
                Log.d(TAG, "找到传感器服务：${sensorService.uuid}")

                // 查找通知特征
                notifyCharacteristic = sensorService.characteristics.find { char ->
                    char.uuid.toString().contains("2a19", ignoreCase = true) ||
                            (char.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0
                }

                // 查找写入特征
                writeCharacteristic = sensorService.characteristics.find { char ->
                    (char.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0
                }

                // 启用通知
                notifyCharacteristic?.let { char ->
                    enableNotification(char)
                }
            } else {
                Log.e(TAG, "未找到传感器服务")
                _errorFlow.value = BluetoothError.ServiceNotFound
            }
        }
    }

    /**
     * 启用特征通知
     */
    private fun enableNotification(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.let { gatt ->
            // 设置本地通知
            gatt.setCharacteristicNotification(characteristic, true)

            // 写入描述符
            val descriptor = characteristic.getDescriptor(
                BluetoothGattDescriptor.CLIENT_CHARACTERISTIC_CONFIG
            )
            descriptor?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeDescriptor(it, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    @Suppress("DEPRECATION")
                    it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(it)
                }
            }
        }
    }

    /**
     * 解析传感器数据包
     * 支持多种数据格式
     */
    private fun parseSensorData(data: ByteArray) {
        try {
            Log.d(TAG, "解析数据：${data.size} bytes")

            when (data.size) {
                // 格式 1: 9 个 float (36 bytes) - 加速度 + 陀螺仪 + 磁力计
                36 -> parseFormat9Float(data)

                // 格式 2: 6 个 float (24 bytes) - 加速度 + 陀螺仪
                24 -> parseFormat6Float(data)

                // 格式 3: 12 个 short (24 bytes) - 原始传感器数据
                24 -> parseFormat12Short(data)

                // 格式 4: 自定义格式
                else -> parseCustomFormat(data)
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析数据失败", e)
            _errorFlow.value = BluetoothError.DataParseError(e.message)
        }
    }

    /**
     * 格式 1: 9 个 float (36 bytes)
     * ax, ay, az, gx, gy, gz, mx, my, mz
     */
    private fun parseFormat9Float(data: ByteArray) {
        val ax = bytesToFloat(data, 0)
        val ay = bytesToFloat(data, 4)
        val az = bytesToFloat(data, 8)
        val gx = bytesToFloat(data, 12)
        val gy = bytesToFloat(data, 16)
        val gz = bytesToFloat(data, 20)
        val mx = bytesToFloat(data, 24)
        val my = bytesToFloat(data, 28)
        val mz = bytesToFloat(data, 32)

        emitSensorData(ax, ay, az, gx, gy, gz, mx, my, mz)
    }

    /**
     * 格式 2: 6 个 float (24 bytes)
     * ax, ay, az, gx, gy, gz
     */
    private fun parseFormat6Float(data: ByteArray) {
        val ax = bytesToFloat(data, 0)
        val ay = bytesToFloat(data, 4)
        val az = bytesToFloat(data, 8)
        val gx = bytesToFloat(data, 12)
        val gy = bytesToFloat(data, 16)
        val gz = bytesToFloat(data, 20)

        emitSensorData(ax, ay, az, gx, gy, gz, 0f, 0f, 0f)
    }

    /**
     * 格式 3: 12 个 short (24 bytes)
     * 原始传感器数据，需要转换
     */
    private fun parseFormat12Short(data: ByteArray) {
        val ax = bytesToShort(data, 0) / 1000f
        val ay = bytesToShort(data, 2) / 1000f
        val az = bytesToShort(data, 4) / 1000f
        val gx = bytesToShort(data, 6) / 1000f
        val gy = bytesToShort(data, 8) / 1000f
        val gz = bytesToShort(data, 10) / 1000f
        val mx = bytesToShort(data, 12) / 10f
        val my = bytesToShort(data, 14) / 10f
        val mz = bytesToShort(data, 16) / 10f

        emitSensorData(ax, ay, az, gx, gy, gz, mx, my, mz)
    }

    /**
     * 格式 4: 自定义格式
     */
    private fun parseCustomFormat(data: ByteArray) {
        // 根据实际硬件协议解析
        Log.w(TAG, "未知数据格式：${data.size} bytes")
    }

    /**
     * 发送传感器数据
     */
    private fun emitSensorData(
        ax: Float, ay: Float, az: Float,
        gx: Float, gy: Float, gz: Float,
        mx: Float, my: Float, mz: Float
    ) {
        val packet = SensorDataPacket(
            accelerometer = Accelerometer(ax, ay, az),
            gyroscope = Gyroscope(gx, gy, gz),
            magnetometer = Magnetometer(mx, my, mz),
            timestamp = System.currentTimeMillis()
        )
        _sensorDataFlow.value = packet
    }

    /**
     * ByteArray 转 Float (小端序)
     */
    private fun bytesToFloat(bytes: ByteArray, offset: Int): Float {
        return java.nio.ByteBuffer.wrap(bytes, offset, 4)
            .order(java.nio.ByteOrder.LITTLE_ENDIAN)
            .float
    }

    /**
     * ByteArray 转 Short (小端序)
     */
    private fun bytesToShort(bytes: ByteArray, offset: Int): Short {
        return java.nio.ByteBuffer.wrap(bytes, offset, 2)
            .order(java.nio.ByteOrder.LITTLE_ENDIAN)
            .short
    }

    /**
     * 连接状态转字符串
     */
    private fun connectionStateToString(state: Int): String {
        return when (state) {
            BluetoothProfile.STATE_CONNECTED -> "CONNECTED"
            BluetoothProfile.STATE_CONNECTING -> "CONNECTING"
            BluetoothProfile.STATE_DISCONNECTED -> "DISCONNECTED"
            BluetoothProfile.STATE_DISCONNECTING -> "DISCONNECTING"
            else -> "UNKNOWN($state)"
        }
    }

    // ==================== 状态和数据类 ====================

    /**
     * 蓝牙连接状态
     */
    enum class BluetoothState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        SCANNING
    }

    /**
     * 蓝牙错误
     */
    sealed class BluetoothError {
        data class ScanFailed(val errorCode: Int) : BluetoothError()
        data class ServiceDiscoveryFailed(val status: Int) : BluetoothError()
        data class WriteFailed(val status: Int) : BluetoothError()
        data class DataParseError(val message: String?) : BluetoothError()
        object InvalidDeviceAddress : BluetoothError()
        object ServiceNotFound : BluetoothError()
    }

    /**
     * 传感器数据包
     */
    data class SensorDataPacket(
        val accelerometer: Accelerometer,
        val gyroscope: Gyroscope,
        val magnetometer: Magnetometer,
        val timestamp: Long
    )

    data class Accelerometer(val x: Float, val y: Float, val z: Float)
    data class Gyroscope(val x: Float, val y: Float, val z: Float)
    data class Magnetometer(val x: Float, val y: Float, val z: Float)

    // ==================== 工具方法 ====================

    /**
     * 检查是否已连接
     */
    fun isConnected(): Boolean {
        return _connectionState.value == BluetoothState.CONNECTED
    }

    /**
     * 获取已连接的设备
     */
    fun getConnectedDevice(): BluetoothDevice? {
        return bluetoothGatt?.device
    }

    /**
     * 获取配对设备列表
     */
    fun getBondedDevices(): Set<BluetoothDevice> {
        return bluetoothAdapter?.bondedDevices ?: emptySet()
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _errorFlow.value = null
    }

    /**
     * 释放资源
     */
    fun destroy() {
        Log.d(TAG, "销毁蓝牙管理器")
        disconnect()
    }

    companion object {
        /**
         * 获取蓝牙适配器
         */
        fun getBtAdapter(): BluetoothAdapter? {
            return BluetoothAdapter.getBtAdapter()
        }

        /**
         * 检查蓝牙是否可用
         */
        fun isBluetoothAvailable(): Boolean {
            val adapter = getBtAdapter()
            return adapter?.isEnabled == true
        }

        /**
         * 检查是否有蓝牙权限
         */
        fun hasBluetoothPermissions(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                android.Manifest.permission.BLUETOOTH_SCAN in context.packageManager.queryPermissionsByFeature(
                    android.content.pm.PackageManager.PERMISSION_GRANTED
                )
            } else {
                true
            }
        }
    }
}
