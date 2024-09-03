package br.com.mleslie.inmovies.network

import br.com.mleslie.inmovies.model.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieDbService {
    @GET("movie/popular")
    fun getPopularMovies(@Query("language") language: String = "en-US", @Query("page") page: Int = 1): Call<MovieResponse>
}
