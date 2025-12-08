package com.example.olenchenkovproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.example.olenchenkovproject.databinding.FragmentFilterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet(private val currentTab: String) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFilterBottomSheetBinding
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentTab == "GENRE") binding.containerGenre.visibility = View.GONE
        if (currentTab == "THEME") binding.containerTheme.visibility = View.GONE

        val langs = listOf("Всі", "Українська", "Англійська", "Мовонезалежна")
        val players = listOf("Всі", "1", "2", "3", "4", "5+")
        val complexes = listOf("Всі", "Легкі", "Середні", "Складні")
        val publishers = listOf("Всі", "Igames", "Kilogames", "Geekach Games", "Hobby World", "Feelindigo", "Days of Wonder")

        val genres = listOf("Всі") + viewModel.allGenres
        val themes = listOf("Всі") + viewModel.allThemes

        val mechanics = listOf("Всі", "Dice Rolling", "Roll and write", "Take that", "Абстрактна стратегія", "Асиметрична стратегія", "Викладання тайлів", "Гра в слова", "Драфт", "Збір сетів", "Картковобудівна", "Колодобудівна", "Кооперативна гра", "Міплплейсмент", "Побудова двигуна", "Пісочниця", "Тактична гра")

        binding.spinLang.adapter = makeAdapter(langs)
        binding.spinPlayers.adapter = makeAdapter(players)
        binding.spinComplex.adapter = makeAdapter(complexes)
        binding.spinPublisher.adapter = makeAdapter(publishers)
        binding.spinGenre.adapter = makeAdapter(genres)
        binding.spinTheme.adapter = makeAdapter(themes)
        binding.spinMechanic.adapter = makeAdapter(mechanics)

        binding.btnApply.setOnClickListener {
            val state = FilterState()

            state.language = binding.spinLang.selectedItem.toString()
            state.playerCount = binding.spinPlayers.selectedItem.toString()
            state.complexity = binding.spinComplex.selectedItem.toString()
            state.publisher = binding.spinPublisher.selectedItem.toString()
            state.genre = binding.spinGenre.selectedItem.toString()
            state.theme = binding.spinTheme.selectedItem.toString()
            state.mechanic = binding.spinMechanic.selectedItem.toString()

            viewModel.filters.value = state
            dismiss()
        }

        binding.btnReset.setOnClickListener {
            viewModel.filters.value = FilterState()
            dismiss()
        }
    }

    private fun makeAdapter(list: List<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
}