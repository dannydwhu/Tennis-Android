package com.smartform.tennis.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 击球记录实体（本地存储）
 */
@Entity(
    tableName = "shots",
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["userId"]),
        Index(value = ["shotType"]),
        Index(value = ["timestamp"])
    ]
)
data class ShotEntity(
    @PrimaryKey val id: Long,
    val sessionId: Long,
    val userId: Long,
    val shotType: String,  // FOREHAND, BACKHAND, SLICE, SERVE, FOREHAND_VOLLEY, BACKHAND_VOLLEY
    val maxSpeed: Double,
    val avgSpeed: Double,
    val timestamp: Long,
    val qualityScore: Int,
    val confidenceScore: Float,
    val aiSuggestion: String?,
    val swingDurationMs: Int,
    val isSynced: Boolean = false  // 是否已同步到服务器
)
