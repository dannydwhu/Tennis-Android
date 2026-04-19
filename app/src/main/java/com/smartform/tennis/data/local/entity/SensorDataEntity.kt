package com.smartform.tennis.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 传感器原始数据实体（本地缓存，用于离线存储）
 */
@Entity(
    tableName = "sensor_raw_data",
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["timestamp"])
    ]
)
data class SensorDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val timestamp: Long,
    val sequenceNumber: Long,
    val ax: Double,
    val ay: Double,
    val az: Double,
    val gx: Double,
    val gy: Double,
    val gz: Double,
    val mx: Double,
    val my: Double,
    val mz: Double,
    val isSynced: Boolean = false  // 是否已同步到服务器
)
