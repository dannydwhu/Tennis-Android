package com.smartform.tennis.data.model

import com.google.gson.annotations.SerializedName

/**
 * 排行榜条目
 */
data class LeaderboardEntry(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("totalShots") val totalShots: Int?,
    @SerializedName("count") val count: Int?,
    @SerializedName("maxSpeed") val maxSpeed: Double?,
    @SerializedName("level") val level: Int?,
    @SerializedName("rank") val rank: Int?
)

/**
 * 我的排名响应
 */
data class MyRankResponse(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("totalShots") val totalShots: Int,
    @SerializedName("totalShotsRank") val totalShotsRank: Long,
    @SerializedName("maxSpeed") val maxSpeed: Double,
    @SerializedName("maxSpeedRank") val maxSpeedRank: Long
)
