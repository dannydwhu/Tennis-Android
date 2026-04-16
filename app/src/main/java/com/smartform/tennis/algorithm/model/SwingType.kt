package com.smartform.tennis.algorithm.model

/**
 * 击球类型枚举
 *
 * 六种网球技术动作
 */
enum class SwingType(val displayName: String) {
    FOREHAND("正手击球"),
    BACKHAND("反手击球"),
    SLICE("切削"),
    SERVE("高压/发球"),
    FOREHAND_VOLLEY("正手截击"),
    BACKHAND_VOLLEY("反手截击"),
    UNKNOWN("未知")
}
