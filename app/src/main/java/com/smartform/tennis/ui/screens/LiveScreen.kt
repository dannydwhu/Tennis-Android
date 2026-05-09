package com.smartform.tennis.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==================== Design Tokens ====================

private val BrandGreen = Color(0xFF00D68F)
private val TechBlue = Color(0xFF3366FF)
private val DarkBg = Color(0xFF0D1117)
private val CardBg = Color(0xFF161B22)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0x99FFFFFF)
private val TextHelper = Color(0x66FFFFFF)

// Stroke type colors
private val StrokeForehand = BrandGreen
private val StrokeBackhand = TechBlue
private val StrokeSlice = Color(0xFFFFD600)
private val StrokeServe = Color(0xFFFF6B35)
private val StrokeForehandVolley = Color(0xFF9B51E0)
private val StrokeBackhandVolley = Color(0xFF00BCD4)

// ==================== Main Screen ====================

@Composable
fun LiveScreen(
    modifier: Modifier = Modifier,
    stats: LiveStats = LiveStats(),
    onSettingsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onStartTraining: () -> Unit = {},
    onConnectDevice: () -> Unit = {}
) {
    // Tab state
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Fixed Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBg)
                .padding(horizontal = 16.dp)
        ) {
            // Top Nav Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎾",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Smartform",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                // Settings icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onSettingsClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚙️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Notification icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onNotificationClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔔", fontSize = 20.sp)
                }
            }
        }

        // Scrollable Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Level Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                LevelCard(
                    level = 5,
                    shotsToNextLevel = 12,
                    progress = 0.78f
                )
            }

            // Training Mode Card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                TrainingCard(
                    lastSwingResult = if (stats.totalShots > 0) "今日已训练 ${stats.totalShots} 次" else "等待训练开始...",
                    onStartTraining = onStartTraining
                )
            }

            // Tab Selector
            item {
                Spacer(modifier = Modifier.height(12.dp))
                TabSelector(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // Stats Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                StatsCard(
                    stats = stats
                )
            }

            // Connect Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ConnectButton(onClick = onConnectDevice)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==================== Components ====================

@Composable
private fun LevelCard(
    level: Int,
    shotsToNextLevel: Int,
    progress: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800),
        label = "levelProgress"
    )

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Level $level",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "再击 $shotsToNextLevel 球升级",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(DarkBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(BrandGreen)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "➡️", fontSize = 20.sp)
        }
    }
}

@Composable
private fun TrainingCard(
    lastSwingResult: String,
    onStartTraining: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎾", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "训练模式",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Start Training Button
            Button(
                onClick = onStartTraining,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "开始训练",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Latest识别
            Text(
                text = "最新识别",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = lastSwingResult,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandGreen
            )
        }
    }
}

@Composable
private fun TabSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            listOf("今日数据", "历史数据").forEachIndexed { index, tab ->
                val isSelected = index == selectedTab
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) BrandGreen.copy(alpha = 0.2f) else Color.Transparent,
                    animationSpec = tween(200),
                    label = "tabBg"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(backgroundColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(index) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) BrandGreen else TextHelper
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCard(stats: LiveStats) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "击球统计",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stroke stats grid - 2 columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left column
                Column(modifier = Modifier.weight(1f)) {
                    StrokeStatItem(
                        name = "正手",
                        count = stats.forehandCount,
                        maxSpeed = stats.forehandMaxSpeed,
                        color = StrokeForehand
                    )
                    StrokeStatItem(
                        name = "反手",
                        count = stats.backhandCount,
                        maxSpeed = stats.backhandMaxSpeed,
                        color = StrokeBackhand
                    )
                    StrokeStatItem(
                        name = "切削",
                        count = stats.sliceCount,
                        maxSpeed = stats.sliceMaxSpeed,
                        color = StrokeSlice
                    )
                }
                // Right column
                Column(modifier = Modifier.weight(1f)) {
                    StrokeStatItem(
                        name = "高压",
                        count = stats.serveCount,
                        maxSpeed = stats.serveMaxSpeed,
                        color = StrokeServe
                    )
                    StrokeStatItem(
                        name = "正手截击",
                        count = stats.forehandVolleyCount,
                        maxSpeed = stats.forehandVolleyMaxSpeed,
                        color = StrokeForehandVolley
                    )
                    StrokeStatItem(
                        name = "反手截击",
                        count = stats.backhandVolleyCount,
                        maxSpeed = stats.backhandVolleyMaxSpeed,
                        color = StrokeBackhandVolley
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TextHelper.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Total and Max Speed row
            Row(modifier = Modifier.fillMaxWidth()) {
                // Total Shots
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "总击球",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = stats.totalShots.toString(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreen,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "次",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                // Max Speed
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "最大速度",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (stats.maxSpeed > 0) stats.maxSpeed.toInt().toString() else "-",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = StrokeServe,
                            fontFamily = FontFamily.Monospace
                        )
                        if (stats.maxSpeed > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "km/h",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StrokeStatItem(
    name: String,
    count: Int,
    maxSpeed: Float,
    color: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = DarkBg,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(10.dp))
            // Name
            Text(
                text = name,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
            // Count
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Speed
            Text(
                text = if (maxSpeed > 0) "${maxSpeed.toInt()}" else "-",
                fontSize = 12.sp,
                color = TextHelper,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ConnectButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandGreen
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "连接设备",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ==================== Data Model ====================

data class LiveStats(
    val totalShots: Int = 0,
    val forehandCount: Int = 0,
    val backhandCount: Int = 0,
    val sliceCount: Int = 0,
    val serveCount: Int = 0,
    val forehandVolleyCount: Int = 0,
    val backhandVolleyCount: Int = 0,
    val maxSpeed: Float = 0f,
    val forehandMaxSpeed: Float = 0f,
    val backhandMaxSpeed: Float = 0f,
    val sliceMaxSpeed: Float = 0f,
    val serveMaxSpeed: Float = 0f,
    val forehandVolleyMaxSpeed: Float = 0f,
    val backhandVolleyMaxSpeed: Float = 0f
)