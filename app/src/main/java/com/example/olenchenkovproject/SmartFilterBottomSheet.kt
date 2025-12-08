package com.example.olenchenkovproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.olenchenkovproject.databinding.FragmentSmartFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SmartFilterBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSmartFilterBinding
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSmartFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentFilter = viewModel.smartFilter.value
        if (currentFilter != null) {
            if (currentFilter.playerCount > 0) binding.etPlayerCount.setText(currentFilter.playerCount.toString())
            if (currentFilter.maxTime > 0) binding.etTime.setText(currentFilter.maxTime.toString())
        }

        binding.btnApply.setOnClickListener {
            val players = binding.etPlayerCount.text.toString().toIntOrNull() ?: 0
            val time = binding.etTime.text.toString().toIntOrNull() ?: 0

            viewModel.smartFilter.value = SmartFilterState(players, time)
            dismiss()
        }

        binding.btnReset.setOnClickListener {
            viewModel.smartFilter.value = SmartFilterState(0, 0)
            dismiss()
        }
    }
}