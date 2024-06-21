package com.raf.catalist.users.repository

import com.raf.catalist.db.AppDatabase
import com.raf.catalist.db.user.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val database: AppDatabase
) {

    suspend fun fetchUsers() : User = database.userDao().getUser()

    fun observeUser() = database.userDao().observeUser()
    fun createUser(firstName: String, lastName: String, mail: String, username: String) {
        database.userDao().insert(User(firstName = firstName, lastName = lastName, mail = mail, username = username))
    }
    fun getUser() = database.userDao().getUser()
    fun updateUser(user: User) = database.userDao().updateUser(user)


}