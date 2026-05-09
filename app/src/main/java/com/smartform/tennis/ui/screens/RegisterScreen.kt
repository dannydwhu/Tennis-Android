package com.smartform.tennis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==================== Design Tokens ====================

private val BrandGreen = Color(0xFF00D68F)
private val DarkBg = Color(0xFF0D1117)
private val CardBg = Color(0xFF161B22)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0x99FFFFFF)
private val TextHelper = Color(0x66FFFFFF)
private val ErrorRed = Color(0xFFFF6B6B)

// ==================== Register Screen ====================

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRegister: (phone: String, password: String, nickname: String?, tennisLevel: String?) -> Unit = { _, _, _, _ -> },
    onNavigateToLogin: () -> Unit = {}
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("BEGINNER") }
    var localError by remember { mutableStateOf<String?>(null) }

    val levels = listOf("BEGINNER" to "新手", "AMATEUR" to "业余", "ADVANCED" to "高手", "PRO" to "专业")
    val displayError = errorMessage ?: localError

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header
        Text(
            text = "创建账号",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "加入 Smartform Tennis",
            fontSize = 16.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Phone Input
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("手机号") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = BrandGreen,
                unfocusedBorderColor = TextHelper,
                focusedLabelColor = BrandGreen,
                unfocusedLabelColor = TextSecondary,
                cursorColor = BrandGreen
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = BrandGreen,
                unfocusedBorderColor = TextHelper,
                focusedLabelColor = BrandGreen,
                unfocusedLabelColor = TextSecondary,
                cursorColor = BrandGreen
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("确认密码") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = BrandGreen,
                unfocusedBorderColor = TextHelper,
                focusedLabelColor = BrandGreen,
                unfocusedLabelColor = TextSecondary,
                cursorColor = BrandGreen
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nickname Input
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("昵称（选填）") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = BrandGreen,
                unfocusedBorderColor = TextHelper,
                focusedLabelColor = BrandGreen,
                unfocusedLabelColor = TextSecondary,
                cursorColor = BrandGreen
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tennis Level Selection
        Text(
            text = "网球水平",
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            levels.forEach { (value, label) ->
                LevelChip(
                    modifier = Modifier.weight(1f),
                    label = label,
                    selected = selectedLevel == value,
                    onClick = { selectedLevel = value }
                )
            }
        }

        // Error Message
        if (displayError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = displayError,
                color = ErrorRed,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Register Button
        Button(
            onClick = {
                // Validate passwords match
                if (password != confirmPassword) {
                    localError = "两次密码输入不一致"
                    return@Button
                }
                if (password.length < 6) {
                    localError = "密码长度不能少于6位"
                    return@Button
                }
                localError = null
                onRegister(phone, password, nickname.ifBlank { null }, selectedLevel)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !isLoading && phone.length >= 11 && password.length >= 6,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGreen,
                contentColor = DarkBg,
                disabledContainerColor = BrandGreen.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = DarkBg,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "注册",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Link
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "已有账号？",
                color = TextSecondary,
                fontSize = 14.sp
            )
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "立即登录",
                    color = BrandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LevelChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = if (selected) BrandGreen.copy(alpha = 0.2f) else CardBg,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (selected) BrandGreen else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
