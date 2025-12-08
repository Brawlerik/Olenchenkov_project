package com.example.olenchenkovproject

import java.io.Serializable

data class BoardGame(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val description: String,
    // ТТХ
    val players: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val playTime: String,
    val minTime: Int,
    val price: Int,
    val year: Int,
    val age: String,
    val publisher: String,
    val textDependency: String,
    val language: String,
    val isLocalization: Boolean,
    val genres: List<String>,
    val mechanics: List<String>,
    val themes: List<String>,
    val series: String?,
    val complexity: Double,
    val rating: Double,
    val imageUrl: String,
    val gameplayImageUrl: String,
    val shopUrl: String,
    val rulesUrl: String,
    var isFavorite: Boolean = false
) : Serializable