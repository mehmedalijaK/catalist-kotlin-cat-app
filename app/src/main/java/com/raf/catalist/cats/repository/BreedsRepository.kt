package com.raf.catalist.cats.repository

import android.util.Log
import androidx.room.withTransaction
import com.raf.catalist.cats.api.BreedsApi
import com.raf.catalist.cats.api.model.BreedApiModel
import com.raf.catalist.cats.api.model.ImageApiModel
import com.raf.catalist.cats.list.mappers.asBreedDbModel
import com.raf.catalist.cats.list.mappers.asImageDbModel
import com.raf.catalist.cats.list.model.BreedUiModel
import com.raf.catalist.db.AppDatabase
import com.raf.catalist.db.breed.Breed
import com.raf.catalist.db.breed.Image
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

// Single static instance -> single source of truth for Breeds
class BreedsRepository @Inject constructor(
    private val database: AppDatabase,
    private val breedsApi: BreedsApi
) {

//    private val database by lazy {RmaApp.data}
    private val breeds = MutableStateFlow(listOf<BreedUiModel>())
    private var breedsCached: List<BreedApiModel> = emptyList();

    suspend fun getBreeds() {

        val allBreeds = breedsApi.getAllBreeds()

        val allPhotos = mutableListOf<Image>()
        allBreeds.forEachIndexed { index, breed ->
            breed.image?.id?.let {
                Image(
                    id = it, height = breed.image.height,
                    width = breed.image.width, url = breed.image.url, breedId = breed.id
                )
            }?.let { allPhotos.add(it) }
        }

        database.withTransaction {
            database.breedDao().upsertAllBreeds(allBreeds
                .map { it.asBreedDbModel() }
                .toMutableList())
            database.breedDao().upsertAllImages(allPhotos)
        }

    }



    suspend fun getImages(breedId: String) {
        val allImages = breedsApi.getBengalImages(limit = 10, breedIds = breedId)
        Log.d("images", allImages.toString())
        database.breedDao().insertAllImages(allImages.map { it.asImageDbModel(breedId) })
    }

    suspend fun getImagesBreed(breedId: String): List<Image> {
        var images : List<Image> = database.breedDao().getImagesBreed(breedId)
        if(images.isEmpty()){
            getImages(breedId)
            images = database.breedDao().getImagesBreed(breedId)
        }
        return images
    }




//    fun allBreeds(): List<BreedData> = breeds.value

//    StateFlow represents a value that changes over time, it emits current value to collectors
//    whenever it is updated. It is immutable, when we want to change the state we have to
//    emit a new value using the emit() method. It has asynchronous update.
//    .asStateFlow() we are creating flow that can be observed. We're exposing read-only view of
//    of the mutable state.

//    suspend fun fetchBreeds(){
//        delay(3)
//        breeds.update{ SampleData.toMutableList() }
//    }

    suspend fun fetchBreedDetails(breedId: String) : BreedUiModel? {
        val breed = database.breedDao().getBreed(breedId)
        return breed.asBreedUiModel()
    }

    fun observeBreedsFlow() = database.breedDao().observeBreeds() // With this ViewModel can observe
    fun observeBreeds(): Flow<List<BreedUiModel>> = breeds.asStateFlow()
    fun observeBreedFlow(breedId: String) = database.breedDao().getBreedFlow(breedId)
    fun observeAlbumsFlow(breedId: String) = database.breedDao().getImageFlow(breedId = breedId)

    fun filterData(catName: String){
        breeds.update {
            if (catName.isEmpty()) {
                // Return all breeds if catName is empty
                database.breedDao().getAllBreeds().map { it.asBreedUiModel() }
            } else {
                ( database.breedDao().getAllBreeds().map { it.asBreedUiModel() }).filter { it.name.contains(catName, ignoreCase = true) }
            }
        }
    }

    fun getImage(imageId: String): Image? = database.breedDao().getImage(imageId)

//    fun observeImageFlow() = database.breedDao().getBreedFlow(breedId)

    private fun Breed.asBreedUiModel() = BreedUiModel(
        id = this.id,
        name = this.name,
        altNames = this.altNames,
        origin = this.origin,
        wikipediaUrl = this.wikipediaUrl,
        description = this.description,
        temperament = this.temperament,
        lifeSpan = this.lifeSpan,
        weight = this.weight,
        rare = this.rare,
        affectionLevel = this.affectionLevel,
        dogFriendly = this.dogFriendly,
        energyLevel = this.energyLevel,
        sheddingLevel = this.sheddingLevel,
        childFriendly = this.childFriendly,
        image = this.coverImageId?.let { getImage(it) }
    )

    private fun Image.asImageUiModel() =
        com.raf.catalist.cats.api.model.Image(
        id = this.id,
        width = this.width,
        height = this.height,
        url = this.url
    )



}