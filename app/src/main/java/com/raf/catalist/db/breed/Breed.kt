package com.raf.catalist.db.breed

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raf.catalist.cats.api.model.Weight
import kotlinx.serialization.Serializable

@Entity
data class Breed(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val altNames: String,
    val origin: String = "",
    val wikipediaUrl: String = "",
    val description: String = "",
    val temperament: String,
    val imageId: String?,
    val lifeSpan: String,
    @Embedded val weight: Weight,
    val rare: Int,
    val affectionLevel: Int,
    val dogFriendly: Int,
    val energyLevel: Int,
    val sheddingLevel: Int,
    val childFriendly: Int,
)