package com.raf.catalist.cats.repository

import com.raf.catalist.cats.domain.BreedData
import kotlinx.coroutines.flow.MutableStateFlow

// Single static instance -> single source of truth for Breeds
object BreedsRepository {

    private val breeds = MutableStateFlow(listOf<BreedData>())

    fun allBreeds(): List<BreedData> = breeds.value

}