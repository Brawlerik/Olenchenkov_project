package com.example.olenchenkovproject

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.olenchenkovproject.FavoriteGameEntity
import com.example.olenchenkovproject.databinding.ItemGameBinding
import com.example.olenchenkovproject.databinding.ItemGameReviewBinding

class GameAdapter(
    private var games: List<BoardGame>,
    private val onGameClick: (BoardGame) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_GAME = 0
    private val VIEW_TYPE_REVIEW = 1

    var isReviewMode = false

    private var reviewsData: Map<Int, FavoriteGameEntity> = emptyMap()

    inner class GameViewHolder(val binding: ItemGameBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ReviewViewHolder(val binding: ItemGameReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (isReviewMode) VIEW_TYPE_REVIEW else VIEW_TYPE_GAME
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_REVIEW) {
            val binding = ItemGameReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReviewViewHolder(binding)
        } else {
            val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GameViewHolder(binding)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val game = games[position]
        val reviewEntry = reviewsData[game.id]

        if (holder is ReviewViewHolder) {
            with(holder.binding) {
                tvReviewTitle.text = game.title
                val rating = reviewEntry?.userRating ?: 0
                val plays = reviewEntry?.playCount ?: 0
                val comment = reviewEntry?.userComment ?: ""

                tvReviewRating.text = if (rating > 0) "⭐ $rating/10" else "Без оцінки"
                tvReviewPlays.text = "🎮 Зіграно: $plays"

                if (comment.isNotEmpty()) {
                    tvReviewComment.text = "\"$comment\""
                    tvReviewComment.visibility = View.VISIBLE
                } else {
                    tvReviewComment.visibility = View.GONE
                }
                Glide.with(root.context).load(game.imageUrl).centerCrop().into(ivReviewImage)
                root.setOnClickListener { onGameClick(game) }
            }

        } else if (holder is GameViewHolder) {
            with(holder.binding) {
                tvTitle.text = game.title
                tvPrice.text = "${game.price} грн"
                tvInfo.text = "${game.year} • ${game.players} • ${game.playTime}"

                val complexityText = "Складність: ${game.complexity}/5"
                tvComplexity.text = complexityText

                val color = when {
                    game.complexity <= 2.0 -> Color.parseColor("#4CAF50")
                    game.complexity <= 3.2 -> Color.parseColor("#FFC107")
                    else -> Color.parseColor("#F44336")
                }
                tvComplexity.setTextColor(color)

                Glide.with(root.context)
                    .load(game.imageUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .into(ivGameImage)


                val myRating = reviewEntry?.userRating ?: 0
                if (myRating > 0) {
                    cvUserRatingBadge.visibility = View.VISIBLE
                    tvUserRatingBadge.text = myRating.toString()
                } else {
                    cvUserRatingBadge.visibility = View.GONE
                }

                root.setOnClickListener { onGameClick(game) }
            }
        }
    }

    override fun getItemCount(): Int = games.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newGames: List<BoardGame>) {
        games = newGames
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateReviewsData(reviews: List<FavoriteGameEntity>) {
        reviewsData = reviews.associateBy { it.gameId }
        notifyDataSetChanged()
    }


}