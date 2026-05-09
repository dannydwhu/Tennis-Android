package com.smartform.tennis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

// ==================== Login Screen ====================

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onLogin: (phone: String, password: String) -> Unit = { _, _ -> },
    onNavigateToRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Logo / Title
        Text(
            text = "🎾",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Smartform Tennis",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "智能网球训练",
            fontSize = 16.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

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

        // Error Message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = ErrorRed,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { onLogin(phone, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !isLoading && phone.isNotBlank() && password.isNotBlank(),
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
                    text = "登录",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Forgot Password
        TextButton(onClick = onForgotPassword) {
            Text(
                text = "忘记密码？",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Register Link
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "还没有账号？",
                color = TextSecondary,
                fontSize = 14.sp
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "立即注册",
                    color = BrandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
