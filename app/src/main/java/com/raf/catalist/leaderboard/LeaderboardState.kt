package com.raf.catalist.leaderboard

import com.raf.catalist.cats.list.model.BreedUiModel

data class LeaderboardState(
    val loading: Boolean = false,
    val leaders: List<LeaderboardUiModel> = emptyList(),
    val error: ListError? = null,
    val query: String = ""
) {
    sealed class ListError{
        data class LoadingListFailed(val cause: Throwable? = null) : ListError()
    }
}