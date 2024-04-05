package com.raf.catalist.cats.repository

import com.raf.catalist.cats.domain.BreedData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.seconds

// Single static instance -> single source of truth for Breeds
object BreedsRepository {

    private val breeds = MutableStateFlow(listOf<BreedData>())

    fun allBreeds(): List<BreedData> = breeds.value

//    StateFlow represents a value that changes over time, it emits current value to collectors
//    whenever it is updated. It is immutable, when we want to change the state we have to
//    emit a new value using the emit() method. It has asynchronous update.
//    .asStateFlow() we are creating flow that can be observed. We're exposing read-only view of
//    of the mutable state.

    suspend fun fetchBreeds(){
        delay(3)
        breeds.update{ SampleData.toMutableList() }
    }

    suspend fun fetchBreedDetails(breedId: String) {
        delay(1.seconds)
    }

    fun observeBreeds(): Flow<List<BreedData>> = breeds.asStateFlow()
    fun observeBreedDetails(breedId: String): Flow<BreedData?> {
        return observeBreeds()
            .map { breeds -> breeds.find { it.id == breedId } }
            .distinctUntilChanged()
    }
}