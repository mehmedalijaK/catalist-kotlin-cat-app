package com.raf.catalist.cats.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.api.model.Weight
import com.raf.catalist.cats.list.model.BreedUiModel
import com.raf.catalist.cats.repository.BreedsRepository
import com.raf.catalist.db.breed.Breed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BreedsListViewModel @Inject constructor(
    private val repository: BreedsRepository
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
        observeBreedsFlow()
        fetchBreeds()
    }

    private fun observeBreedsFlow() {
        // We are launching a new coroutine
        viewModelScope.launch {
            // Which will observe all changes to our passwords
            withContext(Dispatchers.IO){
                repository.observeBreedsFlow().distinctUntilChanged().collect {
                    setState { copy(breeds = it.map {it.asBreedUiModel()}) }
                }
            }

        }
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
                        setState { copy(query = it.catName)}
                    }
                }
            }
        }
    }

    private fun filterData(catName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.filterData(catName)
            }
        }
    }

    private fun fetchBreeds() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.getBreeds()
                }
//                setState { copy(breeds = breeds.map { it.asBreedUiModel() }) }
            } catch (error: IOException) {
                setState { copy(error = BreedsListState.ListError.LoadingListFailed(cause = error)) }
            } finally {
                setState { copy(loading = false) }
            }
            Weight
        }
    }

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
        image = this.coverImageId?.let { repository.getImage(it) }
    )

}