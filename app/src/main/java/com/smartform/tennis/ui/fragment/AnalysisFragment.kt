package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.smartform.tennis.databinding.FragmentAnalysisBinding

/**
 * ANALYSIS 页面 - 技术分析
 */
class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        // 分析类型选择
        binding.serveCard.setOnClickListener {
            showToast("发球分析")
        }
        binding.forehandCard.setOnClickListener {
            showToast("正手分析")
        }
        binding.backhandCard.setOnClickListener {
            showToast("反手分析")
        }

        // 上传视频
        binding.uploadButton.setOnClickListener {
            showToast("从相册选择视频")
        }

        // 录制视频
        binding.recordButton.setOnClickListener {
            showToast("录制视频")
        }

        // 获取更多次数
        binding.getMoreButton.setOnClickListener {
            showToast("获取更多分析次数")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
