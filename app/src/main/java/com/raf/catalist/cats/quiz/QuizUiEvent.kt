package com.raf.catalist.cats.quiz

import com.raf.catalist.cats.list.BreedListUiEvent
import com.raf.catalist.cats.quiz.model.Answer
import com.raf.catalist.cats.quiz.model.UserAnswer
import com.raf.catalist.db.game.Game
import com.raf.catalist.leaderboard.model.QuizResultUser

sealed class QuizUiEvent {
    data class nextQuestion(val result: UserAnswer) : QuizUiEvent()
    data class postGame(val game: Game) : QuizUiEvent()
    data class postOnline(val quizResultUser: QuizResultUser) : QuizUiEvent()
//    data class RequestDataFilter(val catName: String) : BreedListUiEvent()

}