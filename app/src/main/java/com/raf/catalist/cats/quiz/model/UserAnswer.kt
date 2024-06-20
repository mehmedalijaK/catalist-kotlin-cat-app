package com.raf.catalist.cats.quiz.model

import com.raf.catalist.cats.quiz.QuizUiEvent

data class UserAnswer(
    val questionNo: Int,
    val indexAnswer: Int,
    val timeRemaining: Int
)
