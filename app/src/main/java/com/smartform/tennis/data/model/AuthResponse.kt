package com.smartform.tennis.data.model

import com.google.gson.annotations.SerializedName

/**
 * 认证响应（包含令牌和用户信息）
 */
data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiresIn") val expiresIn: Long,
    @SerializedName("user") val user: User,
    @SerializedName("isNewUser") val isNewUser: Boolean
)

/**
 * 令牌对（用于刷新）
 */
data class TokenPair(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiresIn") val expiresIn: Long
)
