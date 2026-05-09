package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smartform.tennis.R
import com.smartform.tennis.ui.screens.LiveScreen
import com.smartform.tennis.ui.screens.LiveStats
import com.smartform.tennis.ui.viewmodel.MainViewModel

/**
 * LIVE 页面 - 首页展示
 * 使用 Jetpack Compose 实现
 */
class LiveFragment : Fragment() {

    private var viewModel: MainViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val statsState = remember { mutableStateOf(LiveStats()) }

                // Observe stats from ViewModel
                LaunchedEffect(viewModel) {
                    viewModel?.todayStats?.collect { stats ->
                        statsState.value = LiveStats(
                            totalShots = stats.totalShots,
                            forehandCount = stats.forehandCount,
                            backhandCount = stats.backhandCount,
                            sliceCount = stats.sliceCount,
                            serveCount = stats.serveCount,
                            forehandVolleyCount = stats.forehandVolleyCount,
                            backhandVolleyCount = stats.backhandVolleyCount,
                            maxSpeed = stats.maxSpeed,
                            forehandMaxSpeed = stats.forehandMaxSpeed,
                            backhandMaxSpeed = stats.backhandMaxSpeed,
                            sliceMaxSpeed = stats.sliceMaxSpeed,
                            serveMaxSpeed = stats.serveMaxSpeed,
                            forehandVolleyMaxSpeed = stats.forehandVolleyMaxSpeed,
                            backhandVolleyMaxSpeed = stats.backhandVolleyMaxSpeed
                        )
                    }
                }

                LiveScreen(
                    stats = statsState.value,
                    onSettingsClick = { showToast("设置") },
                    onNotificationClick = { showToast("通知") },
                    onStartTraining = { navigateToTraining() },
                    onConnectDevice = { showToast("连接设备") }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToTraining() {
        val trainingFragment = TrainingFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, trainingFragment)
            .addToBackStack(null)
            .commit()

        trainingFragment.startTraining()
    }
}