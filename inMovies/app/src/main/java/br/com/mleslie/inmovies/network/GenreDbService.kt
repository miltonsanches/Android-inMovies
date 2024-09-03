package br.com.mleslie.inmovies.network

import br.com.mleslie.inmovies.model.GenreResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GenreDbService {
    @GET("genre/movie/list")
    fun getGenres(): Call<GenreResponse>
}