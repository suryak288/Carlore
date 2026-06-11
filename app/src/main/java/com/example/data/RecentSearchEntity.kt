package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val regNumber: String,
    val title: String,
    val info: String,
    val pill1Title: String,
    val pill1Color: String,
    val pill2Title: String,
    val pill2Color: String,
    val trustScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)
