package com.raf.catalist.leaderboard

data class LeaderboardUiModel(
    val category: Int,
    val nickname: String,
    val result: Double,
    val createdAt: Long
)
