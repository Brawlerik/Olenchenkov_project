package com.example.olenchenkovproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.olenchenkovproject.databinding.FragmentStatsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StatsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentStatsBottomSheetBinding
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStatsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.favoriteGamesIds.observe(viewLifecycleOwner) { allEntities ->
            val mode = viewModel.currentTabMode

            val countColl = allEntities.count { it.status == "COLLECTION" }
            val countWish = allEntities.count { it.status == "WISHLIST" }
            val countFav = allEntities.count { it.isFavorite }

            val filteredEntities = when (mode) {
                "ALL" -> allEntities
                "FAVORITE" -> allEntities.filter { it.isFavorite }
                "COLLECTION" -> allEntities.filter { it.status == "COLLECTION" }
                "WISHLIST" -> allEntities.filter { it.status == "WISHLIST" }
                else -> allEntities
            }

            val ids = filteredEntities.map { it.gameId }
            val gamesToAnalyze = viewModel.games.filter { it.id in ids }

            binding.tvTitle.text = when(mode) {
                "ALL" -> "📊 Вся статистика"
                "FAVORITE" -> "❤️ Статистика улюблених"
                "COLLECTION" -> "📚 Статистика колекції"
                "WISHLIST" -> "💸 Статистика вішліста"
                else -> "Статистика"
            }

            if (mode == "ALL" || mode == "FAVORITE") {
                binding.llDetailedCounts.visibility = View.VISIBLE
                binding.tvCountCollection.text = "📚 В колекції: $countColl"
                binding.tvCountWishlist.text = "⭐ У вішлісті: $countWish"
                binding.tvCountFavorites.text = "❤️ Улюблених: $countFav"
            } else {
                binding.llDetailedCounts.visibility = View.GONE
            }

            if (gamesToAnalyze.isEmpty()) {
                binding.tvTotalValue.text = "0 грн"
                binding.tvTotalCount.text = "0"
                binding.tvAvgComplexity.text = "-"
                binding.tvTopGenre.text = "Улюблений жанр: -"
            } else {
                val totalCount = gamesToAnalyze.size
                val totalPrice = gamesToAnalyze.sumOf { it.price }
                val avgComplexity = gamesToAnalyze.map { it.complexity }.average()

                val allGenres = gamesToAnalyze.flatMap { it.genres }
                val topGenreEntry = allGenres.groupingBy { it }.eachCount().maxByOrNull { it.value }
                val topGenre = topGenreEntry?.key ?: "-"

                binding.tvTotalValue.text = "$totalPrice грн"
                binding.tvTotalCount.text = "$totalCount"
                binding.tvAvgComplexity.text = String.format("%.1f / 5", avgComplexity)
                binding.tvTopGenre.text = "Улюблений жанр: $topGenre"
            }
        }

        binding.btnClose.setOnClickListener { dismiss() }
    }
}