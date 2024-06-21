package com.raf.catalist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raf.catalist.db.breed.Breed
import com.raf.catalist.db.breed.BreedDao
import com.raf.catalist.db.breed.Image
import com.raf.catalist.db.game.Game
import com.raf.catalist.db.game.GameDao
import com.raf.catalist.db.user.User
import com.raf.catalist.db.user.UserDao

@Database(
    entities = [
        Breed::class,
        User::class,
        Image::class,
        Game::class
    ],
    version = 13,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun breedDao(): BreedDao
    abstract fun userDao() : UserDao
    abstract fun gameDao() : GameDao
}