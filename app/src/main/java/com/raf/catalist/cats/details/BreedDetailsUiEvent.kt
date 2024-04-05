package com.raf.catalist.cats.details

sealed class BreedDetailsUiEvent {
    data class RequestPasswordDelete(val passwordId: String) : BreedDetailsUiEvent()
}