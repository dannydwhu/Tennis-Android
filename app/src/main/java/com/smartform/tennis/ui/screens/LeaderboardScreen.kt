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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
private val DarkBg = Color(0xFF0D1117)
private val CardBg = Color(0xFF161B22)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0x99FFFFFF)
private val TextHelper = Color(0x66FFFFFF)
private val GoldColor = Color(0xFFFFD700)
private val SilverColor = Color(0xFFC0C0C0)
private val BronzeColor = Color(0xFFCD7F32)

// ==================== Main Screen ====================

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    top10: List<RankItemUi> = emptyList(),
    currentUser: RankItemUi? = null,
    thirdPlaceScore: Int = 0,
    selectedTab: Int = 0,
    tabs: List<String> = listOf("周榜", "月榜", "总榜"),
    onTabSelected: (Int) -> Unit = {}
) {
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
            // Title
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "排行榜",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Tab selector
            TabSelector(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "加载中...",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Content
        val user = currentUser
        if (user != null && user.rank <= 10) {
            // User in Top 10 - show highlighted list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(top10) { index, item ->
                    val isMe = item.rank == user.rank
                    LeaderboardItem(
                        item = item,
                        isMe = isMe
                    )
                    if (index < top10.lastIndex) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // User outside Top 10 - show Top 10 + fixed bottom rank
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(top10) { index, item ->
                    LeaderboardItem(
                        item = item,
                        isMe = false
                    )
                    if (index < top10.lastIndex) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Fixed bottom bar for my rank
            if (user != null) {
                MyRankFixedBar(
                    rank = user.rank,
                    name = user.name,
                    score = user.score,
                    gapText = if (thirdPlaceScore > user.score) "距第3名还差 ${thirdPlaceScore - user.score} 球" else ""
                )
            }
        }
    }
}

// ==================== Components ====================

@Composable
private fun TabSelector(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tab ->
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
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            color = if (isSelected) BrandGreen else TextHelper
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardItem(
    item: RankItemUi,
    isMe: Boolean
) {
    val backgroundColor = if (isMe) BrandGreen.copy(alpha = 0.15f) else CardBg

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                val rankDisplay = when (item.rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> item.rank.toString()
                }
                Text(
                    text = rankDisplay,
                    fontSize = if (item.rank <= 3) 20.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (item.rank) {
                        1 -> GoldColor
                        2 -> SilverColor
                        3 -> BronzeColor
                        else -> TextPrimary
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isMe) BrandGreen.copy(alpha = 0.3f) else CardBg.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.avatar.ifEmpty { "🥎" },
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name
            Text(
                text = if (isMe) "我" else item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isMe) BrandGreen else TextPrimary,
                modifier = Modifier.weight(1f)
            )

            // Score
            Text(
                text = "${item.score} 次",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun MyRankFixedBar(
    rank: Int,
    name: String,
    score: Int,
    gapText: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BrandGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BrandGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🧑",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = "$score 次",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Gap text
                if (gapText.isNotEmpty()) {
                    Text(
                        text = gapText,
                        fontSize = 12.sp,
                        color = TextHelper
                    )
                }
            }
        }
    }
}

// ==================== Data Model ====================

data class RankItemUi(
    val rank: Int,
    val name: String,
    val score: Int,
    val avatar: String = ""
)

/**
 * Leaderboard entry data class for API integration
 */
data class LeaderboardEntryUi(
    val userId: Long,
    val nickname: String?,
    val totalShots: Int,
    val level: Int?,
    val rank: Int
)

/**
 * My rank data class for API integration
 */
data class MyRankUi(
    val userId: Long,
    val nickname: String?,
    val totalShots: Int,
    val totalShotsRank: Long,
    val maxSpeed: Double,
    val maxSpeedRank: Long
)