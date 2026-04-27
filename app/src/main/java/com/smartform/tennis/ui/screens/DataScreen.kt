package com.smartform.tennis.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ==================== Design Tokens ====================

// Colors
private val BrandGreen = Color(0xFF00D68F)
private val TechBlue = Color(0xFF3366FF)
private val BurstOrange = Color(0xFFFF6B35)
private val DarkBg = Color(0xFF0D1117)
private val CardBg = Color(0xFF161B22)
private val ModuleBg = Color(0xFF1E242C)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0x99FFFFFF)
private val TextHelper = Color(0x66FFFFFF)
private val White05 = Color(0x0DFFFFFF)
private val White1A = Color(0x1AFFFFFF)

// Semantic Colors for Stroke Types
private val StrokeForehand = BrandGreen
private val StrokeBackhand = TechBlue
private val StrokeSlice = Color(0xFFFFD600)
private val StrokeServe = BurstOrange
private val StrokeForehandVolley = Color(0xFF9B51E0)
private val StrokeBackhandVolley = Color(0xFF00BCD4)

// ==================== Main Screen ====================

@Composable
fun DataScreen(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("今日", "本周", "本月", "本年")

    // Mock data - all zeros as per spec
    var maxSpeed by remember { mutableIntStateOf(0) }
    var totalShots by remember { mutableIntStateOf(0) }
    var duration by remember { mutableFloatStateOf(0f) }
    var calories by remember { mutableIntStateOf(0) }

    // Ability values (5 dimensions)
    var abilityValues by remember { mutableStateOf(listOf(0f, 0f, 0f, 0f, 0f)) }

    // Main content with fixed header
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Fixed Header (NavBar + Tabs)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBg)
                .padding(horizontal = 16.dp)
        ) {
            TopNavBar()
            Spacer(modifier = Modifier.height(12.dp))
            TimeRangeTabs(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        // Scrollable Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Combined Metrics Card (MaxSpeed + Shots + Duration + Calories)
            item {
                Spacer(modifier = Modifier.height(2.dp))
                CombinedMetricsCard(
                    maxSpeed = maxSpeed,
                    totalShots = totalShots,
                    duration = duration,
                    calories = calories
                )
            }

            // Stroke Grid
            item {
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    text = "击球动作",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = TextPrimary
//                )
                Spacer(modifier = Modifier.height(2.dp))
                StrokeGrid()
            }

            // Ability Radar Chart
            item {
                Spacer(modifier = Modifier.height(2.dp))
                AbilityRadarChart(values = abilityValues)
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

// ==================== Components ====================

@Composable
private fun TopNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBg)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🎾",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "Smartform",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
private fun TimeRangeTabs(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    // Container with border using TechBlue as secondary color
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = TechBlue.copy(alpha = 0.3f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                )
            },
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedTab
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) BrandGreen.copy(alpha = 0.15f) else Color.Transparent,
                    animationSpec = tween(200),
                    label = "tabBackground"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
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
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        color = if (isSelected) TextPrimary else TextHelper
                    )
                }
            }
        }
    }
}

@Composable
private fun CombinedMetricsCard(
    maxSpeed: Int,
    totalShots: Int,
    duration: Float,
    calories: Int
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = maxSpeed.toFloat(),
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "speedAnimation"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Row 1: Max Speed + Total Shots (equal width)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Max Speed
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "最大速度",
                        fontSize = 12.sp,
                        color = TextHelper
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = if (maxSpeed == 0) "0" else animatedSpeed.toInt().toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "km/h",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                // Total Shots
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "击球数",
                        fontSize = 12.sp,
                        color = TextHelper
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = totalShots.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "个",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Duration + Calories (equal width)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Duration
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "运动时长",
                        fontSize = 12.sp,
                        color = TextHelper
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = if (duration == 0f) "0" else String.format("%.1f", duration),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        if (duration > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "h",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }

                // Calories
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "卡路里消耗",
                        fontSize = 12.sp,
                        color = TextHelper
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = calories.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "kcal",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar below
            SpeedProgressBar(progress = min(maxSpeed / 200f, 1f))
        }
    }
}

@Composable
private fun SpeedProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "progressAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(CardBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BrandGreen)
        )
    }
}

@Composable
private fun StrokeGrid() {
    val strokeTypes = listOf(
        StrokeTypeItem("正手", 0, StrokeForehand),
        StrokeTypeItem("反手", 0, StrokeBackhand),
        StrokeTypeItem("切削", 0, StrokeSlice),
        StrokeTypeItem("高压", 0, StrokeServe),
        StrokeTypeItem("正手截击", 0, StrokeForehandVolley),
        StrokeTypeItem("反手截击", 0, StrokeBackhandVolley)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            strokeTypes[0].let {
                StrokeModule(
                    modifier = Modifier.weight(1f),
                    name = it.name,
                    count = it.count,
                    color = it.color
                )
            }
            strokeTypes[1].let {
                StrokeModule(
                    modifier = Modifier.weight(1f),
                    name = it.name,
                    count = it.count,
                    color = it.color
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            strokeTypes[2].let {
                StrokeModule(
                    modifier = Modifier.weight(1f),
                    name = it.name,
                    count = it.count,
                    color = it.color
                )
            }
            strokeTypes[3].let {
                StrokeModule(
                    modifier = Modifier.weight(1f),
                    name = it.name,
                    count = it.count,
                    color = it.color
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            strokeTypes[4].let {
                StrokeModule(
                    modifier = Modifier.weight(1f),
                    name = it.name,
                    count = it.count,
                    color = it.color
                )
            }
            strokeTypes[5].let {
                StrokeModule(
                    modifier = Modifier.weight(1f),
                    name = it.name,
                    count = it.count,
                    color = it.color
                )
            }
        }
    }
}

private data class StrokeTypeItem(
    val name: String,
    val count: Int,
    val color: Color
)

@Composable
private fun StrokeModule(
    modifier: Modifier = Modifier,
    name: String,
    count: Int,
    color: Color
) {
    val animatedCount by animateFloatAsState(
        targetValue = count.toFloat(),
        animationSpec = tween(600),
        label = "countAnimation"
    )

    Surface(
        modifier = modifier
            .height(60.dp),
        color = ModuleBg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (count == 0) "0" else animatedCount.toInt().toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun AbilityRadarChart(values: List<Float>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        color = CardBg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Title inside the box - top left
            Text(
                text = "能力分析",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )

            // Radar Chart centered
            RadarChart(
                values = values,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun RadarChart(values: List<Float>, modifier: Modifier = Modifier) {
    val dimensions = listOf("爆发力", "进攻性", "活跃度", "对抗性", "耐力值")
    val displayValues = if (values.all { it == 0f }) listOf(5f, 5f, 5f, 5f, 5f) else values

    Box(modifier = modifier.size(100.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 2 - 12.dp.toPx()
            val angleStep = (2 * PI / 5).toFloat()
            val startAngle = (-PI / 2).toFloat()

            // Draw background pentagon layers (3 layers)
            for (layer in 1..3) {
                val layerRadius = radius * layer / 3
                val path = Path()
                for (i in 0 until 5) {
                    val angle = startAngle + i * angleStep
                    val x = centerX + layerRadius * cos(angle)
                    val y = centerY + layerRadius * sin(angle)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(
                    path = path,
                    color = White05,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Draw dimension axis lines
            for (i in 0 until 5) {
                val angle = startAngle + i * angleStep
                val endX = centerX + radius * cos(angle)
                val endY = centerY + radius * sin(angle)
                drawLine(
                    color = White1A,
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))
                )
            }

            // Draw center point (empty state)
            drawCircle(
                color = BrandGreen,
                radius = 4.dp.toPx(),
                center = Offset(centerX, centerY)
            )
        }

        // Dimension labels - positioned around the chart
        val labelPositions = listOf(
            Offset(0.5f, 0.0f),   // 爆发力 - top
            Offset(0.85f, 0.35f),  // 进攻性 - top right
            Offset(0.75f, 0.75f),  // 活跃度 - bottom right
            Offset(0.25f, 0.75f),  // 对抗性 - bottom left
            Offset(0.15f, 0.35f)   // 耐力值 - top left
        )

        labelPositions.forEachIndexed { index, position ->
            Text(
                text = dimensions[index],
                fontSize = 10.sp,
                color = TextHelper,
                modifier = Modifier
                    .offset(
                        x = (100 * position.x - 10).dp,
                        y = (100 * position.y - 3).dp
                    )
            )
        }
    }
}

