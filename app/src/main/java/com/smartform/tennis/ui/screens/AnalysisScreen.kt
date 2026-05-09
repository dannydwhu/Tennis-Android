package com.smartform.tennis.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
private val StrokeServe = Color(0xFFFF6B35)
private val StrokeForehand = BrandGreen
private val StrokeBackhand = Color(0xFF3366FF)
private val DarkBg = Color(0xFF0D1117)
private val CardBg = Color(0xFF161B22)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0x99FFFFFF)
private val TextHelper = Color(0x66FFFFFF)

// ==================== Main Screen ====================

@Composable
fun AnalysisScreen(
    modifier: Modifier = Modifier,
    onServeClick: () -> Unit = {},
    onForehandClick: () -> Unit = {},
    onBackhandClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onRecordClick: () -> Unit = {},
    onGetMoreClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Title
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "技术分析",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Available times card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AvailableTimesCard(
                    times = 3,
                    onGetMoreClick = onGetMoreClick
                )
            }

            // Analysis type section title
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "选择分析类型",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Analysis type cards - 3 in a row
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnalysisTypeCard(
                        modifier = Modifier.weight(1f),
                        emoji = "🏀",
                        label = "发球分析",
                        color = StrokeServe,
                        onClick = onServeClick
                    )
                    AnalysisTypeCard(
                        modifier = Modifier.weight(1f),
                        emoji = "🎾",
                        label = "正手分析",
                        color = StrokeForehand,
                        onClick = onForehandClick
                    )
                    AnalysisTypeCard(
                        modifier = Modifier.weight(1f),
                        emoji = "🏸",
                        label = "反手分析",
                        color = StrokeBackhand,
                        onClick = onBackhandClick
                    )
                }
            }

            // Upload video section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                UploadVideoCard(
                    onUploadClick = onUploadClick,
                    onRecordClick = onRecordClick
                )
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
private fun AvailableTimesCard(
    times: Int,
    onGetMoreClick: () -> Unit
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "可用分析次数",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "$times 次",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    fontFamily = FontFamily.Monospace
                )
            }

            OutlinedButton(
                onClick = onGetMoreClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandGreen
                )
            ) {
                Text(text = "获取更多")
            }
        }
    }
}

@Composable
private fun AnalysisTypeCard(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(120.dp),
        color = CardBg,
        shape = RoundedCornerShape(16.dp)
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
            Text(
                text = emoji,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun UploadVideoCard(
    onUploadClick: () -> Unit,
    onRecordClick: () -> Unit
) {
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
                text = "上传视频",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Upload button
            Button(
                onClick = onUploadClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "从相册选择视频",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Record button
            OutlinedButton(
                onClick = onRecordClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "录制视频",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "视频时长限制：30秒 - 5分钟",
                fontSize = 12.sp,
                color = TextHelper
            )
        }
    }
}