package com.raf.catalist.db.game

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val userId: Int,
    val score: Double,
    val rightAnswers: Int
)
