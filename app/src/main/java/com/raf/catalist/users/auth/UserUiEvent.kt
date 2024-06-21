package com.raf.catalist.users.auth

import android.service.notification.NotificationListenerService.Ranking

sealed class UserUiEvent {
    data class CreateUser(
        val firstName: String, val lastName: String, val username: String,  val mail: String
        ) : UserUiEvent()
    data class updateUser(
        val firstName: String, val lastName: String, val username: String,  val mail: String,
        val id: Int?, val ranking: Int?,
    ) : UserUiEvent()
}