package com.raf.catalist.db.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(user: User)

    @Update
    fun updateUser(vararg user: User)

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser() : Flow<User>
}