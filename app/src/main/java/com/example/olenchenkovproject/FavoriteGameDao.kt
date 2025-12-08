package com.example.olenchenkovproject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteGameDao {
    @Query("SELECT * FROM favorite_games")
    fun getAllFavorites(): Flow<List<FavoriteGameEntity>>

    @Query("SELECT * FROM favorite_games WHERE gameId = :id LIMIT 1")
    suspend fun getGameById(id: Int): FavoriteGameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: FavoriteGameEntity)

    @Delete
    suspend fun delete(game: FavoriteGameEntity)

    @Query("SELECT EXISTS(SELECT * FROM favorite_games WHERE gameId = :id)")
    suspend fun isFavorite(id: Int): Boolean
}