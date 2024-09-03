package br.com.mleslie.inmovies.model

// Movie.kt
import java.io.Serializable

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val adult: Boolean,
    val backdrop_path: String?,
    val genre_ids: List<Int>,
    val original_language: String,
    val original_title: String,
    val popularity: Double,
    val release_date: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
) : Serializable
