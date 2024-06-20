package com.raf.catalist.db.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raf.catalist.db.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(game: Game)


    @Query("SELECT * FROM game")
    fun observeGames() : Flow<List<Game>>
}