package com.smartform.tennis.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==================== Design Tokens ====================

private val BrandGreen = Color(0xFF00D68F)
private val DarkBg = Color(0xFF0D1117)
private val CardBg = Color(0xFF161B22)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0x99FFFFFF)
private val TextHelper = Color(0x66FFFFFF)

// ==================== Main Screen ====================

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    nickname: String = "网球达人",
    level: String = "Level 1",
    trainingCount: Int = 0,
    bestSpeed: Double = 0.0,
    totalDays: Int = 0,
    onSettingsClick: () -> Unit = {},
    onSyncClick: () -> Unit = {},
    onFeedbackClick: () -> Unit = {},
    onLegalClick: () -> Unit = {},
    onAboutClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Fixed Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBg)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "我的",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Scrollable Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Profile Card
            item {
                ProfileCard(
                    nickname = nickname,
                    level = level
                )
            }

            // Stats Card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                StatsCard(
                    trainingCount = trainingCount,
                    bestSpeed = bestSpeed,
                    totalDays = totalDays
                )
            }

            // Action Buttons - Row 1
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = null, // Use text emoji
                        label = "设置",
                        onClick = onSettingsClick
                    )
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = null,
                        label = "数据同步",
                        onClick = onSyncClick
                    )
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = null,
                        label = "意见反馈",
                        onClick = onFeedbackClick
                    )
                }
            }

            // Action Buttons - Row 2
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = null,
                        label = "法律条款",
                        onClick = onLegalClick
                    )
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = null,
                        label = "关于我们",
                        onClick = onAboutClick
                    )
                    // Empty space for 3-column layout
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==================== Components ====================

@Composable
private fun ProfileCard(
    nickname: String,
    level: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BrandGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎾",
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name and Level
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nickname,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = level,
                        fontSize = 12.sp,
                        color = BrandGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCard(
    trainingCount: Int,
    bestSpeed: Double,
    totalDays: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            // Row 1: Training Count
            StatRow(
                label = "训练记录",
                value = "${trainingCount}次",
                showDivider = true
            )

            // Row 2: Best Score
            StatRow(
                label = "最好成绩",
                value = "${String.format("%.1f", bestSpeed)} km/h",
                showDivider = true
            )

            // Row 3: Total Days
            StatRow(
                label = "累计天数",
                value = "${totalDays}天",
                showDivider = false
            )
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextSecondary
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 12.dp)
                    .background(TextHelper.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(80.dp),
        color = CardBg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon or emoji
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(TextHelper.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Icon would go here
                }
            } else {
                // Use text emoji as placeholder
                val emoji = when (label) {
                    "设置" -> "⚙️"
                    "数据同步" -> "🔄"
                    "意见反馈" -> "💬"
                    "法律条款" -> "📜"
                    "关于我们" -> "ℹ️"
                    else -> "📱"
                }
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}