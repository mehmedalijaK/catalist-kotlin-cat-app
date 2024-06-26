package com.raf.catalist.cats.api.model

import com.raf.catalist.cats.album.model.AlbumUiModel
import com.raf.catalist.cats.list.model.BreedUiModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class BreedApiModel(
    val id: String,
    val name: String,
    @SerialName("alt_names") val altNames: String = "",
    val description: String,
    val origin: String,
    val temperament: String,
    @SerialName("life_span") val lifeSpan: String,
    val weight: Weight,
    val rare: Int,
    @SerialName("affection_level") val affectionLevel: Int,
    @SerialName("dog_friendly") val dogFriendly: Int,
    @SerialName("energy_level") val energyLevel: Int,
    @SerialName("shedding_level") val sheddingLevel: Int,
    @SerialName("child_friendly") val childFriendly: Int,
    val image: Image? = null,
    @SerialName("wikipedia_url") val wikipediaUrl: String = "",
)

@Serializable
data class ImageApiModel(
    val id: String,
    val url: String,
    val width: Int?,
    val height: Int?
)

@Serializable
data class Weight(
    val imperial: String?,
    val metric: String?
)

@Serializable
data class Image(
    val id: String?,
    val width: Int?,
    val height: Int?,
    val url: String?
)




fun ImageApiModel.asAlbumUiModel() = AlbumUiModel(
    id = this.id,
    url = this.url,
    width = this.width,
    height = this.height
)