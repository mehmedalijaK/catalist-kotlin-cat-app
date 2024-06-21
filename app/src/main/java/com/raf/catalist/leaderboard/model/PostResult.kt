package com.raf.catalist.leaderboard.model

import android.provider.ContactsContract.CommonDataKinds.Nickname
import kotlinx.serialization.Serializable

@Serializable
data class PostResultResponse(
    val result: ResultHelper,
    val ranking: Int,
)

@Serializable
data class ResultHelper(
    val category: Int,
    val nickname: String,
    val result: Double,
    val createdAt: Long
)
