package com.raf.catalist.cats.api

import com.raf.catalist.cats.api.model.BreedApiModel
import retrofit2.http.GET
import retrofit2.http.Path

interface BreedsApi {

    @GET("breeds")
    suspend fun getAllBreeds(): List<BreedApiModel>

//    Did not use getBreedDetails
    @GET("breeds/{breed_id}")
    suspend fun getBreedDetails(
        @Path("breed_id") breedId: String,
    ): BreedApiModel
}