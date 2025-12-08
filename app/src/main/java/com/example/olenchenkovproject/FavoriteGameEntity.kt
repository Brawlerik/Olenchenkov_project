package com.example.olenchenkovproject

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_games")
data class FavoriteGameEntity(
    @PrimaryKey val gameId: Int,
    val status: String = "NONE",
    val isFavorite: Boolean = false,
    val userRating: Int = 0,
    val playCount: Int = 0,
    val userComment: String = "",
    val gameNotes: String = ""
)