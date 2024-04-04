package com.raf.catalist.cats.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.repository.BreedsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class BreedsListViewModel constructor(
    private val repository: BreedsRepository = BreedsRepository
): ViewModel(){

//    Compiler will know type
    private val _state = MutableStateFlow(BreedsListState())

//    Expose state
    val state = _state.asStateFlow()

    private fun setState(reducer: BreedsListState.() -> BreedsListState) = _state.getAndUpdate(reducer)

    init {
        fetchBreeds()
    }

    private fun fetchBreeds() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                val breeds = withContext(Dispatchers.IO) {
                    repository.allBreeds()
                }
                setState { copy(breeds = breeds) }
            } catch (error: IOException) {
                setState { copy(error = BreedsListState.ListError.LoadingListFailed(cause = error)) }
            } finally {
                setState { copy(loading = false) }
            }
        }
    }
}