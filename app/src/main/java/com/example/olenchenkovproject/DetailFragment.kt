package com.example.olenchenkovproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.olenchenkovproject.databinding.FragmentGameDetailBinding

class DetailFragment : Fragment(R.layout.fragment_game_detail) {

    private lateinit var binding: FragmentGameDetailBinding
    private val viewModel: GameViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGameDetailBinding.bind(view)

        @Suppress("DEPRECATION")
        val game = arguments?.getSerializable("game_key") as? BoardGame

        game?.let { currentGame ->
            with(binding) {
                tvDetailTitle.text = currentGame.title

                val genres = currentGame.genres.joinToString(", ")
                val themes = currentGame.themes.joinToString(", ")
                val mechanics = currentGame.mechanics.joinToString(", ")

                val infoBlock = """
                    |🏢 Видавець: ${currentGame.publisher}
                    |📅 Рік: ${currentGame.year}
                    |🌍 Локалізація: ${if(currentGame.isLocalization) "Так" else "Ні"} (${currentGame.language})
                    |📜 Текст: ${currentGame.textDependency}
                    |
                    |🎭 Жанр: $genres
                    |🔮 Тематика: $themes
                    |⚙️ Механіки: $mechanics
                """.trimMargin()

                tvDetailDescription.text = "$infoBlock\n\n📝 ОПИС:\n${currentGame.description}"
                tvDetailPlayers.text = "${currentGame.players} • ${currentGame.playTime} • ${currentGame.age}"
                tvDetailPrice.text = "${currentGame.price} грн"
                tvDetailRating.text = "BGG: ${currentGame.rating}"

                Glide.with(requireContext()).load(currentGame.imageUrl).into(ivDetailImage)
                if (currentGame.gameplayImageUrl.isNotEmpty()) Glide.with(requireContext()).load(currentGame.gameplayImageUrl).into(ivGameplayImage)

                viewModel.favoriteGamesIds.observe(viewLifecycleOwner) { favorites ->
                    val entry = favorites.find { it.gameId == currentGame.id }

                    if (entry != null && entry.isFavorite) {
                        btnFavorite.text = "В улюбленому ❤️"
                        btnFavorite.setIconResource(android.R.drawable.btn_star_big_on)
                        btnFavorite.setBackgroundColor(Color.GRAY) // Сірий
                    } else {
                        btnFavorite.text = "Додати в улюблене"
                        btnFavorite.setIconResource(android.R.drawable.btn_star_big_off)
                        btnFavorite.setBackgroundColor(Color.parseColor("#FF5722"))
                    }

                    toggleGameStatus.clearOnButtonCheckedListeners()
                    if (entry != null) {
                        when (entry.status) {
                            "COLLECTION" -> toggleGameStatus.check(R.id.btnStatusCollection)
                            "WISHLIST" -> toggleGameStatus.check(R.id.btnStatusWishlist)
                            else -> toggleGameStatus.clearChecked()
                        }
                    } else {
                        toggleGameStatus.clearChecked()
                    }

                    toggleGameStatus.addOnButtonCheckedListener { _, checkedId, isChecked ->
                        if (isChecked) {
                            when (checkedId) {
                                R.id.btnStatusCollection -> viewModel.updateStatus(currentGame.id, "COLLECTION")
                                R.id.btnStatusWishlist -> viewModel.updateStatus(currentGame.id, "WISHLIST")
                            }
                        } else {
                            if (toggleGameStatus.checkedButtonId == View.NO_ID) {
                                viewModel.updateStatus(currentGame.id, "NONE")
                            }
                        }
                    }

                    if (entry != null && (entry.userRating > 0 || entry.playCount > 0 || entry.userComment.isNotEmpty())) {
                        btnReview.text = "Редагувати відгук ✏️"
                        cvUserReview.visibility = View.VISIBLE
                        tvReviewRating.text = if (entry.userRating > 0) "⭐ ${entry.userRating}/10" else ""
                        tvReviewPlays.text = if (entry.playCount > 0) "🎮 Партій: ${entry.playCount}" else ""
                        tvReviewComment.text = entry.userComment
                        tvReviewComment.visibility = if (entry.userComment.isNotEmpty()) View.VISIBLE else View.GONE
                    } else {
                        btnReview.text = "Написати відгук ✍️"
                        cvUserReview.visibility = View.GONE
                    }

                    if (entry != null && etGameNotes.text.isEmpty()) {
                        etGameNotes.setText(entry.gameNotes)
                    }
                    if (entry != null && entry.gameNotes.isNotEmpty()) {
                        btnToggleNotes.text = "Мій нотатник (Є записи) 📝"
                        btnToggleNotes.setBackgroundColor(Color.parseColor("#E1BEE7"))
                        btnToggleNotes.setTextColor(Color.parseColor("#4A148C"))
                    } else {
                        btnToggleNotes.text = "Мій нотатник 📝"
                    }
                }

                btnFavorite.setOnClickListener { viewModel.toggleFavorite(currentGame.id) }
                btnToggleNotes.setOnClickListener {
                    cvNotesPanel.visibility = if (cvNotesPanel.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
                btnSaveNotes.setOnClickListener {
                    viewModel.saveNotes(currentGame.id, etGameNotes.text.toString())
                    cvNotesPanel.visibility = View.GONE
                    Toast.makeText(context, "Збережено", Toast.LENGTH_SHORT).show()
                }
                btnReview.setOnClickListener {
                    ReviewBottomSheet(currentGame.id).show(parentFragmentManager, "ReviewSheet")
                }
                btnRules.setOnClickListener { openLink(currentGame.rulesUrl) }
                btnBuy.setOnClickListener { openLink(currentGame.shopUrl) }
            }
        }
    }

    private fun openLink(url: String) {
        if (url.isNotEmpty()) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (_: Exception) {
                Toast.makeText(context, "Помилка посилання", Toast.LENGTH_SHORT).show()
            }
        }
    }
}