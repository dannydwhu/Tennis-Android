package com.smartform.tennis.data.model

import com.google.gson.annotations.SerializedName

/**
 * 传感器数据
 *
 * 用于上传到后端第一数据库（原始数据存储）
 */
data class SensorData(
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("sequenceNumber") val sequenceNumber: Long,
    @SerializedName("ax") val ax: Double,
    @SerializedName("ay") val ay: Double,
    @SerializedName("az") val az: Double,
    @SerializedName("gx") val gx: Double,
    @SerializedName("gy") val gy: Double,
    @SerializedName("gz") val gz: Double,
    @SerializedName("mx") val mx: Double,
    @SerializedName("my") val my: Double,
    @SerializedName("mz") val mz: Double
)