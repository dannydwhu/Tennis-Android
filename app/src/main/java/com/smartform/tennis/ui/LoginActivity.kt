package com.smartform.tennis.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.smartform.tennis.TennisApplication
import com.smartform.tennis.data.model.LoginRequest
import com.smartform.tennis.data.model.RegisterRequest
import com.smartform.tennis.data.network.ApiClient
import com.smartform.tennis.ui.screens.LoginScreen
import com.smartform.tennis.ui.screens.RegisterScreen
import kotlinx.coroutines.launch

/**
 * Login Activity - Handles user authentication
 */
class LoginActivity : ComponentActivity() {

    private val apiService by lazy { ApiClient().apiService }
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already logged in, go to MainActivity directly
        if (TennisApplication.isLoggedIn) {
            navigateToMain()
            return
        }

        showLoginScreen()
    }

    private fun showLoginScreen() {
        isLoginMode = true
        setContent {
            LoginScreen(
                isLoading = false,
                errorMessage = null,
                onLogin = { phone, password ->
                    login(phone, password)
                },
                onNavigateToRegister = {
                    showRegisterScreen()
                },
                onForgotPassword = {
                    Toast.makeText(this, "Forgot password not implemented", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

        private fun showRegisterScreen() {
        isLoginMode = false
        setContent {
            RegisterScreen(
                onRegister = { phone, password, nickname, level ->
                    register(phone, password, nickname, level)
                },
                onNavigateToLogin = {
                    showLoginScreen()
                }
            )
        }
    }

    private fun login(phone: String, password: String) {
        setContent {
            LoginScreen(
                isLoading = true,
                errorMessage = null,
                onLogin = { _, _ -> },
                onNavigateToRegister = { showRegisterScreen() },
                onForgotPassword = { }
            )
        }

        lifecycleScope.launch {
            try {
                val response = apiService.login(
                    LoginRequest(
                        phone = phone,
                        password = password,
                        loginType = "PHONE"
                    )
                )

                if (response.isSuccessful && response.body()?.code == 0) {
                    val authResponse = response.body()?.data
                    if (authResponse != null) {
                        // Save session
                        TennisApplication.saveSession(
                            userId = authResponse.user.id,
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken
                        )
                        navigateToMain()
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Login failed"
                    showLoginScreenWithError(errorMsg)
                }
            } catch (e: Exception) {
                showLoginScreenWithError("Network error: ${e.message}")
            }
        }
    }

    private fun register(phone: String, password: String, nickname: String?, tennisLevel: String?) {
        setContent {
            RegisterScreen(
                isLoading = true,
                errorMessage = null,
                onRegister = { _, _, _, _ -> },
                onNavigateToLogin = { showLoginScreen() }
            )
        }

        lifecycleScope.launch {
            try {
                val request = RegisterRequest(
                    phone = phone,
                    password = password,
                    nickname = nickname,
                    tennisLevel = tennisLevel
                )
                val response = apiService.register(request)

                if (response.isSuccessful && response.body()?.code == 0) {
                    val authResponse = response.body()?.data
                    if (authResponse != null) {
                        TennisApplication.saveSession(
                            userId = authResponse.user.id,
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken
                        )
                        navigateToMain()
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Registration failed"
                    showRegisterScreenWithError(errorMsg)
                }
            } catch (e: Exception) {
                showRegisterScreenWithError("Network error: ${e.message}")
            }
        }
    }

    private fun showLoginScreenWithError(error: String) {
        setContent {
            LoginScreen(
                isLoading = false,
                errorMessage = error,
                onLogin = { phone, password -> login(phone, password) },
                onNavigateToRegister = { showRegisterScreen() },
                onForgotPassword = { }
            )
        }
    }

    private fun showRegisterScreenWithError(error: String) {
        setContent {
            RegisterScreen(
                errorMessage = error,
                onRegister = { phone, password, nickname, level ->
                    register(phone, password, nickname, level)
                },
                onNavigateToLogin = { showLoginScreen() }
            )
        }
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
