package com.raf.catalist.cats.quiz.model

data class GameUiModel (
    val id: Int? = null,
    val userId: Int,
    val score: Double,
    val rightAnswers: Int
)