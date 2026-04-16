package com.smartform.tennis.data.model

import com.google.gson.annotations.SerializedName

/**
 * 击球记录模型
 */
data class Shot(
    @SerializedName("id") val id: Long,
    @SerializedName("sessionId") val sessionId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("shotType") val shotType: ShotType,
    @SerializedName("maxSpeed") val maxSpeed: Double?,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("qualityScore") val qualityScore: Int?,
    @SerializedName("aiSuggestion") val aiSuggestion: String?
) {
    enum class ShotType {
        FOREHAND,           // 正手击球
        BACKHAND,           // 反手击球
        SLICE,              // 切削
        SERVE,              // 高压/发球
        FOREHAND_VOLLEY,    // 正手截击
        BACKHAND_VOLLEY     // 反手截击
    }
}
