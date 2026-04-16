package com.smartform.tennis.data.model

import com.google.gson.annotations.SerializedName

/**
 * 训练会话模型
 */
data class TrainingSession(
    @SerializedName("id") val id: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("totalShots") val totalShots: Int,
    @SerializedName("forehandCount") val forehandCount: Int,
    @SerializedName("backhandCount") val backhandCount: Int,
    @SerializedName("sliceCount") val sliceCount: Int,
    @SerializedName("serveCount") val serveCount: Int,
    @SerializedName("forehandVolleyCount") val forehandVolleyCount: Int,
    @SerializedName("backhandVolleyCount") val backhandVolleyCount: Int,
    @SerializedName("maxSpeed") val maxSpeed: Double?
)
