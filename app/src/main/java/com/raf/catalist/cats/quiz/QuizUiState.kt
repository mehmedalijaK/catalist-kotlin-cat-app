package com.raf.catalist.cats.quiz

import com.raf.catalist.cats.list.model.BreedUiModel
import com.raf.catalist.cats.quiz.model.Answer

data class QuizUiState (
    val loading: Boolean = false,
    val generatingQuestions: Boolean = true,
    val breeds: List<BreedUiModel> = emptyList(),
    val error: ListError? = null,
    val question: List<Answer> = emptyList(),
    val questionNo: Int = 1,
    val score: Int = 0
)

sealed class ListError{
    data class LoadingListFailed(val cause: Throwable? = null) : ListError()
}

