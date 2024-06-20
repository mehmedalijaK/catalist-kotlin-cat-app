package com.raf.catalist.cats.quiz.model

import com.raf.catalist.cats.list.model.BreedUiModel

data class Answer(
    val questionNo: Int,
    val questionText: String,
    val firstBreed: BreedUiModel,
    val secondBreed: BreedUiModel,
    val indexRightAnswer: Int
)
