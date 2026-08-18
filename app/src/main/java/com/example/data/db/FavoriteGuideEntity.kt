package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_guides")
data class FavoriteGuideEntity(
    @PrimaryKey val guideId: String,
    val title: String,
    val category: String,
    val snippet: String,
    val readTimeMinutes: Int = 5,
    val savedAt: Long = System.currentTimeMillis()
)
