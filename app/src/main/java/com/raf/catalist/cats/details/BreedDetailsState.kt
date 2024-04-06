package com.raf.catalist.cats.details

import android.telecom.Call.Details
import com.raf.catalist.cats.domain.BreedData

data class BreedDetailsState (
    val breedId: String,
    val loading: Boolean = false,
    val data: BreedData? = null,
    val error: DetailsError? = null
){
    sealed class DetailsError {
        data class DataFetchFailed(val cause: Throwable? = null) : DetailsError()
    }
}