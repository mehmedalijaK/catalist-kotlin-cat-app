package com.raf.catalist.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.api.model.Weight
import com.raf.catalist.cats.list.BreedsListState
import com.raf.catalist.cats.list.model.BreedUiModel
import com.raf.catalist.cats.repository.BreedsRepository
import com.raf.catalist.db.breed.Breed
import com.raf.catalist.leaderboard.model.QuizResult
import com.raf.catalist.leaderboard.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardState())

    //    Expose state
    val state = _state.asStateFlow()

    private fun setState(reducer: LeaderboardState.() -> LeaderboardState) = _state.getAndUpdate(reducer)


    init {
        fetchLeaderboard()
    }

    private fun fetchLeaderboard() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    val repo = repository.getLeaderboard()
                    setState { copy( leaders = repo.map { it.asLeaderboardUiModel() }) }
                }

            } catch (error: IOException) {
                setState { copy(error = LeaderboardState.ListError.LoadingListFailed(cause = error)) }
            } finally {
                setState { copy(loading = false) }
            }
            Weight
        }
    }

    private fun QuizResult.asLeaderboardUiModel() = LeaderboardUiModel(
        category = this.category,
        nickname = this.nickname,
        result = this.result,
        createdAt = this.createdAt
    )



}