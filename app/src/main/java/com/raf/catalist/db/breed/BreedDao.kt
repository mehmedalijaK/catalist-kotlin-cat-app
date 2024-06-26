package com.raf.catalist.db.breed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedDao{

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(breed: Breed)

    @Insert
    fun insertAll(list: List<Breed>)

    @Upsert
    fun insertAllImages(list: List<Image>)

    @Upsert
    fun upsertAllBreeds(list: List<Breed>)

    @Upsert
    fun upsertAllImages(list: List<Image>)

    @Query("SELECT * FROM Breed")
    fun getAllBreeds() : List<Breed>

    @Query("SELECT * FROM Image WHERE Image.id = :imageId")
    fun getImage(imageId: String): Image

    @Query("SELECT * FROM Breed WHERE Breed.id = :breedId")
    fun getBreed(breedId: String): Breed

    @Query("SELECT * FROM Breed WHERE Breed.id = :breedId")
    fun getBreedFlow(breedId: String): Flow<Breed>

    @Query("SELECT * FROM Image WHERE breedId = :breedId")
    fun getImageFlow(breedId: String): Flow<List<Image>>

    @Query("SELECT * FROM Image WHERE breedId = :breedId")
    fun getImagesBreed(breedId: String): List<Image>

    @Query("SELECT * FROM Breed")
    fun observeBreeds() : Flow<List<Breed>>

}