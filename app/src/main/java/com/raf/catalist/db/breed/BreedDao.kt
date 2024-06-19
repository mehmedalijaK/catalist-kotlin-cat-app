package com.raf.catalist.db.breed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface BreedDao{

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(breed: Breed)

    @Insert
    fun insertAll(list: List<Breed>)
}