package com.raf.catalist.cats.list.mappers

import androidx.room.Embedded
import com.raf.catalist.cats.api.model.BreedApiModel
import com.raf.catalist.cats.api.model.ImageApiModel
import com.raf.catalist.cats.api.model.Weight
import com.raf.catalist.db.breed.Breed
import com.raf.catalist.db.breed.Image

fun BreedApiModel.asBreedDbModel() : Breed {
    return Breed(
        id = this.id,
        name = this.name,
        altNames = this.altNames,
        origin = this.origin,
        wikipediaUrl = this.wikipediaUrl,
        description = this.description,
        temperament = this.temperament,
        coverImageId = this.image?.id,
        lifeSpan = this.lifeSpan,
        weight = this.weight,
        rare = this.rare,
        affectionLevel = this.affectionLevel,
        dogFriendly = this.dogFriendly,
        energyLevel = this.energyLevel,
        sheddingLevel = this.sheddingLevel,
        childFriendly = this.childFriendly
    )
}

fun ImageApiModel.asImageDbModel(breedsId: String) : Image {
    return Image(
        id = this.id,
        width = this.width,
        height = this.height,
        url = this.url,
        breedId = breedsId
    )
}