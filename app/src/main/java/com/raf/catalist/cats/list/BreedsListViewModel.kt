package com.raf.catalist.cats.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.repository.BreedsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
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

//  publishEvent is exposed
    private val events = MutableSharedFlow<BreedListUiEvent>()
    fun publishEvent(event:BreedListUiEvent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    init {
        observeEvents()
        observeBreeds()
        fetchBreeds()
    }

    private fun observeBreeds() {
        // We are launching a new coroutine
        viewModelScope.launch {
            // Which will observe all changes to our passwords
            repository.observeBreeds().collect {
                setState { copy(breeds = it) }
            }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    is BreedListUiEvent.RequestDataFilter -> {
                        filterData(catName = it.catName)
                    }
                }
            }
        }
    }

    private fun filterData(catName: String) {
        viewModelScope.launch {
            repository.filterData(catName)
        }
    }

    private fun fetchBreeds() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.fetchBreeds()
                }
            } catch (error: IOException) {
                setState { copy(error = BreedsListState.ListError.LoadingListFailed(cause = error)) }
            } finally {
                setState { copy(loading = false) }
            }
        }
    }
}