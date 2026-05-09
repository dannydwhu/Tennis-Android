package com.smartform.tennis.data.model

import com.google.gson.annotations.SerializedName

/**
 * 用户注册请求
 */
data class RegisterRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("heightCm") val heightCm: Int? = null,
    @SerializedName("weightKg") val weightKg: Double? = null,
    @SerializedName("tennisLevel") val tennisLevel: String? = null
)
