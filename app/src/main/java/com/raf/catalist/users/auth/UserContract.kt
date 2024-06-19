package com.raf.catalist.users.auth

import com.raf.catalist.users.model.UserUiModel

interface UserContract {
    data class UserUiState(
        val loading: Boolean = true,
        val user: UserUiModel? = null
    )
}