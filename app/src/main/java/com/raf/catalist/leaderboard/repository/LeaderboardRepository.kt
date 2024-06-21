package com.raf.catalist.leaderboard.repository

import android.util.Log
import com.raf.catalist.leaderboard.api.LeaderBoardApi
import com.raf.catalist.leaderboard.model.QuizResult
import com.raf.catalist.leaderboard.model.QuizResultUser
import retrofit
import javax.inject.Inject

class LeaderboardRepository @Inject constructor() {
    private val leaderBoardApi: LeaderBoardApi = retrofit.create(LeaderBoardApi::class.java)
    suspend fun getLeaderboard() : List<QuizResult> = leaderBoardApi.getLeaderboard(3)

    suspend fun postResult(quizResult: QuizResultUser){
        var response = leaderBoardApi.postResult(quizResult)
        Log.d("msg", response.toString())
    }
}