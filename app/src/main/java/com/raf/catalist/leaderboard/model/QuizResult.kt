package com.raf.catalist.leaderboard.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizResult(
    val category: Int,
    val nickname: String,
    val result: Double,
    val createdAt: Long
)

