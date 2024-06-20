package com.raf.catalist.cats.quiz

import com.raf.catalist.cats.quiz.model.GameUiModel

data class QuizHomeUiState (
    val loading: Boolean = false,
    val games: List<GameUiModel> = emptyList(),
)
