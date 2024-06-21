package com.raf.catalist.users.auth

import com.raf.catalist.cats.quiz.model.GameUiModel
import com.raf.catalist.users.model.UserUiModel

interface UserContract {
    data class UserUiState(
        val loading: Boolean = true,
        val user: UserUiModel? = null,
        val games: List<GameUiModel> = emptyList(),
        )
}