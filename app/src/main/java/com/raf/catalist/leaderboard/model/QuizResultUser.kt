package com.raf.catalist.leaderboard.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizResultUser(
    val nickname: String,
    val result: Double,
    val category: Int,
)
