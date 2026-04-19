package com.smartform.tennis.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.smartform.tennis.R

/**
 * DATA 页面 - 传感器数据日志
 */
class DataFragment : Fragment() {

    private var sensorDataLogText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorDataLogText = view.findViewById(R.id.sensorDataLogText)
    }

    /**
     * 更新传感器数据日志
     */
    fun updateSensorLog(message: String) {
        sensorDataLogText?.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensorDataLogText = null
    }
}
