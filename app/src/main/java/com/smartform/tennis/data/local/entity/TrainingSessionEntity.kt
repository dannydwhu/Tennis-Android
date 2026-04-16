package com.smartform.tennis.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 训练会话实体（本地存储）
 */
@Entity(
    tableName = "training_sessions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["startTime"])
    ]
)
data class TrainingSessionEntity(
    @PrimaryKey val id: Long,
    val userId: Long,
    val startTime: Long,
    val endTime: Long?,
    val durationSeconds: Int,
    val totalShots: Int,
    val forehandCount: Int,
    val backhandCount: Int,
    val sliceCount: Int,
    val serveCount: Int,
    val forehandVolleyCount: Int,
    val backhandVolleyCount: Int,
    val maxSpeed: Double?,
    val avgSpeed: Double?,
    val qualityScore: Double?,
    val deviceId: String?,
    val isSynced: Boolean = false  // 是否已同步到服务器
)
