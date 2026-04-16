package com.smartform.tennis.data.model

import com.google.gson.annotations.SerializedName

/**
 * API 通用响应
 */
data class ApiResponse<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?,
    @SerializedName("timestamp") val timestamp: Long
) {
    val isSuccess: Boolean
        get() = code == 0
}