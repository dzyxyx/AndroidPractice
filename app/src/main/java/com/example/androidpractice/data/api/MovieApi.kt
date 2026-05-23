package com.example.androidpractice.data.api

import com.example.androidpractice.data.dto.MovieDto
import com.example.androidpractice.data.dto.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("v1.4/movie")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("type") type: String = "movie",
    ): MovieListResponse

    @GET("v1.4/movie/{id}")
    suspend fun getMovieById(
        @Path("id") id: Int,
    ): MovieDto

    @GET("v1.4/movie/search")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
    ): MovieListResponse
}