package com.raf.catalist.cats.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.list.BreedsListState
import com.raf.catalist.cats.list.model.BreedUiModel
import com.raf.catalist.cats.quiz.model.GameUiModel
import com.raf.catalist.cats.repository.BreedsRepository
import com.raf.catalist.cats.repository.GameRepository
import com.raf.catalist.db.breed.Breed
import com.raf.catalist.db.game.Game
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuizHomeViewModel @Inject constructor(
    private val repositoryGame: GameRepository
) : ViewModel(){

    private val _state = MutableStateFlow(QuizHomeUiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: QuizHomeUiState.() -> QuizHomeUiState) = _state.getAndUpdate(reducer)

    init {
        observeGamesFlow()
    }


    private fun observeGamesFlow() {
        // We are launching a new coroutine
        viewModelScope.launch {
            // Which will observe all changes to our passwords
            withContext(Dispatchers.IO){
                repositoryGame.observeGamesFlow().distinctUntilChanged().collect {
                    setState { copy(games = it.map {it.asGameUiModel()}) }
                }
            }

        }
    }

    private fun Game.asGameUiModel() = GameUiModel(
        id = this.id,
        userId = this.userId,
        rightAnswers = this.rightAnswers,
        score = this.score
    )

}