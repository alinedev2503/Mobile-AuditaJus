package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteGuideDao {
    @Query("SELECT * FROM favorite_guides ORDER BY savedAt DESC")
    fun getAllFavoriteGuides(): Flow<List<FavoriteGuideEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_guides WHERE guideId = :guideId)")
    fun isGuideFavorite(guideId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteGuideEntity)

    @Query("DELETE FROM favorite_guides WHERE guideId = :guideId")
    suspend fun deleteFavoriteById(guideId: String)
}
