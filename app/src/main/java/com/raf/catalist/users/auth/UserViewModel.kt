package com.raf.catalist.users.auth

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.list.BreedListUiEvent
import com.raf.catalist.cats.quiz.model.GameUiModel
import com.raf.catalist.cats.repository.GameRepository
import com.raf.catalist.db.game.Game
import com.raf.catalist.db.user.User
import com.raf.catalist.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.raf.catalist.users.auth.UserContract.UserUiState
import com.raf.catalist.users.model.UserUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@HiltViewModel
class UserViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val repositoryGame: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: UserUiState.() -> UserUiState) = _state.update(reducer)
    private val events = MutableSharedFlow<UserUiEvent>()

    init {
        fetchUsers()
        observeUsers()
        observeEvents()
        observeGamesFlow()
    }

    fun publishEvent(event: UserUiEvent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                events.collect {
                    when (it) {
                        is UserUiEvent.CreateUser -> {
                            userRepository.createUser(firstName = it.firstName, lastName = it.lastName,
                                mail = it.mail, username = it.username)
                        }

                        is UserUiEvent.updateUser -> {
                            userRepository.updateUser(
                                User(
                                    id = it.id,
                                    username = it.username,
                                    mail = it.mail,
                                    lastName = it.lastName,
                                    firstName = it.firstName,
                                    ranking = it.ranking
                                )
                            )
                        }
                    }
                }
            }
        }
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

    private fun fetchUsers(){
        viewModelScope.launch {
            setState { copy(loading = true) }

            try {
                val user = withContext(Dispatchers.IO) {
                    userRepository.fetchUsers()
                }
                Log.d("Print: ",user.toString())
                if(user != null) setState {copy(user = user.asUserUI())}

            }catch (error : Exception){
//                TO DO
            }
            setState { copy(loading = false) }
        }
    }

    private fun observeUsers(){
        viewModelScope.launch {
            userRepository.observeUser().distinctUntilChanged()
                .collect{
                    if(it != null) setState { copy(user = it.asUserUI()) }
                    else setState { copy(user = null) }
                }
        }
    }

    private fun User.asUserUI() = this.ranking?.let {
        UserUiModel(
        id = this.id,
        lastName = this.lastName,
        firstName = this.firstName,
        username = this.username,
        mail = this.mail,
        ranking = it
    )
    }

    private fun Game.asGameUiModel() = GameUiModel(
        id = this.id,
        userId = this.userId,
        rightAnswers = this.rightAnswers,
        score = this.score
    )
}