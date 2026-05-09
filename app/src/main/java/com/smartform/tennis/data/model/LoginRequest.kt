package com.smartform.tennis.data.model

import com.google.gson.annotations.SerializedName

/**
 * 用户登录请求
 */
data class LoginRequest(
    @SerializedName("phone") val phone: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("loginType") val loginType: String = "PHONE",
    @SerializedName("wechatOpenId") val wechatOpenId: String? = null,
    @SerializedName("qqOpenId") val qqOpenId: String? = null
)
