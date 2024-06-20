package com.raf.catalist.cats.repository

import com.raf.catalist.db.AppDatabase
import com.raf.catalist.db.game.Game
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val database: AppDatabase
) {
    suspend fun insertGame(game: Game){
        database.gameDao().insert(game)
    }

    fun observeGamesFlow() = database.gameDao().observeGames()

}