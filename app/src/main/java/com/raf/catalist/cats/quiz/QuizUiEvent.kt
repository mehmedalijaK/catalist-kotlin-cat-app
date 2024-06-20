package com.raf.catalist.cats.quiz

import com.raf.catalist.cats.list.BreedListUiEvent
import com.raf.catalist.cats.quiz.model.Answer

sealed class QuizUiEvent {
    data class setQuestions(val questions: List<Answer>) : QuizUiEvent()

//    data class RequestDataFilter(val catName: String) : BreedListUiEvent()

}