package com.smartform.tennis.data.model

import com.google.gson.annotations.SerializedName

/**
 * 用户模型
 */
data class User(
    @SerializedName("id") val id: Long,
    @SerializedName("phone") val phone: String?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("heightCm") val heightCm: Int?,
    @SerializedName("weightKg") val weightKg: Double?,
    @SerializedName("tennisLevel") val tennisLevel: TennisLevel?,
    @SerializedName("currentLevel") val currentLevel: Int,
    @SerializedName("experiencePoints") val experiencePoints: Long
) {
    enum class TennisLevel {
        BEGINNER,     // 新手
        AMATEUR,      // 业余
        ADVANCED,     // 高手
        PRO           // 专业
    }
}
