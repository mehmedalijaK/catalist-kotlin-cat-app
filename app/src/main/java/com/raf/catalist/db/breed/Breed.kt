package com.raf.catalist.db.breed

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Breed(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val altNames: String,
    val origin: String = "",
    val wikipediaUrl: String = "",
    val description: String = "",
    val temperament: String,
//    val image: Image?,
    val lifeSpan: String,
//    val weight: Weight,
    val rare: Int,
    val affectionLevel: Int,
    val dogFriendly: Int,
    val energyLevel: Int,
    val sheddingLevel: Int,
    val childFriendly: Int,
)
