package com.raf.catalist.leaderboard.api

import com.raf.catalist.leaderboard.model.PostResultResponse
import com.raf.catalist.leaderboard.model.QuizResult
import com.raf.catalist.leaderboard.model.QuizResultUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LeaderBoardApi {

    @GET("leaderboard")
    suspend fun getLeaderboard(
        @Query("category") category: Int
    ): List<QuizResult>

    @POST("leaderboard")
    suspend fun postResult(
        @Body result: QuizResultUser
    ): PostResultResponse
}