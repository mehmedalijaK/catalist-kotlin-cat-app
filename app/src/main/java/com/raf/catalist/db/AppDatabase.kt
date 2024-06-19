package com.raf.catalist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raf.catalist.db.breed.Breed
import com.raf.catalist.db.breed.BreedDao
import com.raf.catalist.db.user.User

@Database(
    entities = [
        Breed::class,
        User::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun breedDao(): BreedDao
}