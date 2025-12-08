package com.example.olenchenkovproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.olenchenkovproject.databinding.FragmentReviewBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReviewBottomSheet(private val gameId: Int) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentReviewBottomSheetBinding
    private val viewModel: GameViewModel by activityViewModels()
    private var currentPlays = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentReviewBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.favoriteGamesIds.observe(viewLifecycleOwner) { favorites ->
            val entry = favorites.find { it.gameId == gameId }
            if (entry != null) {

            }
        }


        val entry = viewModel.favoriteGamesIds.value?.find { it.gameId == gameId }
        if (entry != null) {
            binding.sliderRating.value = entry.userRating.toFloat()
            currentPlays = entry.playCount
            binding.etComment.setText(entry.userComment)
        }
        updatePlays()

        binding.btnMinus.setOnClickListener {
            if (currentPlays > 0) currentPlays--
            updatePlays()
        }
        binding.btnPlus.setOnClickListener {
            currentPlays++
            updatePlays()
        }

        binding.btnSave.setOnClickListener {
            val rating = binding.sliderRating.value.toInt()
            val comment = binding.etComment.text.toString()
            viewModel.saveReview(gameId, rating, comment, currentPlays)
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            // Видалити відгук = зберегти нулі
            viewModel.saveReview(gameId, 0, "", 0)
            dismiss()
        }
    }

    private fun updatePlays() {
        binding.tvPlayCount.text = currentPlays.toString()
    }
}