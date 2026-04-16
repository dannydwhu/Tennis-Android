package com.smartform.tennis.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户实体（本地缓存）
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    val phone: String?,
    val nickname: String?,
    val avatarUrl: String?,
    val heightCm: Int?,
    val weightKg: Double?,
    val tennisLevel: String?,
    val currentLevel: Int,
    val experiencePoints: Long
)
