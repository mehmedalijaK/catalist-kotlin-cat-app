package com.raf.catalist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raf.catalist.cats.list.db.Breed
import com.raf.catalist.cats.list.db.BreedDao

@Database(
    entities = [
        Breed::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun breedDao(): BreedDao
}