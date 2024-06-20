package com.raf.catalist.cats.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.api.model.Weight
import com.raf.catalist.cats.list.BreedListUiEvent
import com.raf.catalist.cats.list.BreedsListState
import com.raf.catalist.cats.list.model.BreedUiModel
import com.raf.catalist.cats.quiz.model.Answer
import com.raf.catalist.cats.repository.BreedsRepository
import com.raf.catalist.cats.repository.GameRepository
import com.raf.catalist.db.breed.Breed
import com.raf.catalist.users.repository.UserRepository
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
class QuizViewModel @Inject constructor(
    private val repository: BreedsRepository,
    private val repositoryGame: GameRepository
) :ViewModel(){
    private val _state = MutableStateFlow(QuizUiState())

    //    Expose state
    val state = _state.asStateFlow()

    private fun setState(reducer: QuizUiState.() -> QuizUiState) = _state.getAndUpdate(reducer)

    //  publishEvent is exposed
    private val events = MutableSharedFlow<QuizUiEvent>()

    fun publishEvent(event:QuizUiEvent) {
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
                    is QuizUiEvent.nextQuestion -> {

                        if(it.result.timeRemaining == 0){
                            if(it.result.indexAnswer != -1) {
                                if (it.result.indexAnswer == state.value.question[it.result.questionNo - 1].indexRightAnswer) {
                                    setState { copy(numRight = numRight+1) }
                                    setState { copy(secondTime = 0) }
                                    setState { copy(done = true) }
                                }else{
                                    setState { copy(secondTime = 0) }
                                    setState { copy(done = true) }
                                }
                            }else{
                                setState { copy(secondTime = 0) }
                                setState { copy(done = true) }
                            }
                        }else{
                            if(it.result.indexAnswer == -1){
                                setState { copy(done = true) }
                                setState { copy(secondTime = it.result.timeRemaining) }
                            }else{
                                if(it.result.indexAnswer == state.value.question[it.result.questionNo-1].indexRightAnswer)
                                    setState { copy (numRight = numRight+1) }

                                if(it.result.questionNo == 20){
                                    setState { copy(done = true) }
                                    setState { copy(secondTime = it.result.timeRemaining) }
                                    Log.d("time", it.result.timeRemaining.toString())
                                }else {
                                    setState { copy( questionNo = (it.result.questionNo+1))}
                                }
                            }
                        }

                    }
                    is QuizUiEvent.postGame -> {
                        withContext(Dispatchers.IO){
                            repositoryGame.insertGame(it.game)
                        }
                    }
                }
            }
        }
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

    private fun fetchBreeds() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.getBreeds()
                }
                generateQuiz()
//                setState { copy(breeds = breeds.map { it.asBreedUiModel() }) }
            } catch (error: IOException) {
//                setState { copy(error = BreedsListState.ListError.LoadingListFailed(cause = error)) }
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

    private fun generateQuiz() {

        val questions = listOf(
            "Which cat is heavier on average?",
            "Which cat lives longer on average?",
        )

        val breeds: List<BreedUiModel> = state.value.breeds
        val answers: MutableList<Answer> = mutableListOf() // Changed to MutableList

        var i: Int = 1
        repeat(20) {
            val randomBreeds: List<BreedUiModel> = getRandomBreeds(breeds)
            val randomQuestion = questions.random()

            if (randomQuestion.equals(questions[0])) {
                val weightFirstBreed = randomBreeds[0].weight.metric?.split(" - ")?.firstOrNull()?.toIntOrNull()
                val weightSecondBreed = randomBreeds[1].weight.metric?.split(" - ")?.firstOrNull()?.toIntOrNull()

                val indexRightAnswer = if (weightFirstBreed != null && weightSecondBreed != null) {
                    if (weightFirstBreed > weightSecondBreed) 0 else 1
                } else -1

                answers.add( // Use add directly on the mutable list
                    Answer(
                        questionNo = i,
                        questionText = "Which cat is heavier on average?",
                        firstBreed = randomBreeds[0],
                        secondBreed = randomBreeds[1],
                        indexRightAnswer = indexRightAnswer
                    )
                )
            } else {
                val lifeSpanFirstBreed = randomBreeds[0].lifeSpan?.split(" - ")?.firstOrNull()?.toIntOrNull()
                val lifeSpanSecondBreed = randomBreeds[1].lifeSpan?.split(" - ")?.firstOrNull()?.toIntOrNull()

                val indexRightAnswer = if (lifeSpanFirstBreed != null && lifeSpanSecondBreed != null) {
                    if (lifeSpanFirstBreed > lifeSpanSecondBreed) 0 else 1
                } else -1

                answers.add( // Use add directly on the mutable list
                    Answer(
                        questionNo = i,
                        questionText = "Which cat lives longer on average?",
                        firstBreed = randomBreeds[0],
                        secondBreed = randomBreeds[1],
                        indexRightAnswer = indexRightAnswer
                    )
                )
            }

            i++
        }
        setState { copy( question = answers)}
        setState { copy (generatingQuestions = false) }
        Log.d("questions", answers.toString())
    }

}