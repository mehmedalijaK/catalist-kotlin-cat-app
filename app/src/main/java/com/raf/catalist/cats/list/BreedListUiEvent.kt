package com.raf.catalist.cats.list

sealed class BreedListUiEvent {
    data class RequestDataFilter(val catName: String) : BreedListUiEvent()
}