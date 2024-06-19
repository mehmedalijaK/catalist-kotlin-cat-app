package com.raf.catalist.users.auth

sealed class UserUiEvent {
    data class CreateUser(
        val firstName: String, val lastName: String, val username: String,  val mail: String
        ) : UserUiEvent()
}