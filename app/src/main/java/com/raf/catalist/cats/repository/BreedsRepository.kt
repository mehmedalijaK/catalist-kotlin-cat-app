package com.raf.catalist.cats.repository

import com.raf.catalist.cats.api.BreedsApi
import com.raf.catalist.cats.api.model.BreedApiModel
import com.raf.catalist.cats.api.model.Image
import com.raf.catalist.cats.api.model.ImageApiModel
import com.raf.catalist.cats.api.model.Weight
import com.raf.catalist.cats.domain.BreedData
import com.raf.catalist.cats.list.model.BreedUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import retrofit
import kotlin.time.Duration.Companion.seconds

// Single static instance -> single source of truth for Breeds
object BreedsRepository {

    private val breedsApi: BreedsApi = retrofit.create(BreedsApi::class.java)
    private val breeds = MutableStateFlow(listOf<BreedUiModel>())
    private var breedsCached: List<BreedApiModel> = emptyList();

    suspend fun getBreeds() {
        breedsCached = breedsApi.getAllBreeds() // We will fetch all breeds and have them locally cached.
        breeds.update { breedsCached.map { it.asBreedUiModel() } } // Observer from BreedsListViewModel will trigger on this
    }

    suspend fun getImages(breedId: String) : List<ImageApiModel> {
        return breedsApi.getBengalImages(limit = 10, breedIds = breedId)
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

    suspend fun fetchBreedDetails(breedId: String) {
        delay(1.seconds)
    }

    fun observeBreeds(): Flow<List<BreedUiModel>> = breeds.asStateFlow() // With this ViewModel can observe
    fun observeBreedDetails(breedId: String): Flow<BreedUiModel?> {
        return observeBreeds()
            .map { breeds -> breeds.find { it.id == breedId } }
            .distinctUntilChanged()
    }

    fun filterData(catName: String){
        breeds.update {
            if (catName.isEmpty()) {
                // Return all breeds if catName is empty
                breedsCached.map { it.asBreedUiModel() }
            } else {
                ( breedsCached.map { it.asBreedUiModel() }).filter { it.name.contains(catName, ignoreCase = true) }
            }
        }
    }


    private fun BreedApiModel.asBreedUiModel() = BreedUiModel(
        id = this.id,
        name = this.name,
        altNames = this.altNames,
        origin = this.origin,
        wikipediaUrl = this.wikipediaUrl,
        description = this.description,
        temperament = this.temperament,
        image = this.image,
        lifeSpan = this.lifeSpan,
        weight = this.weight,
        rare = this.rare,
        affectionLevel = this.affectionLevel,
        dogFriendly = this.dogFriendly,
        energyLevel = this.energyLevel,
        sheddingLevel = this.sheddingLevel,
        childFriendly = this.childFriendly,
    )

}