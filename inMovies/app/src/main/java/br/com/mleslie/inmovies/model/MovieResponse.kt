package br.com.mleslie.inmovies.model

import br.com.mleslie.inmovies.model.Movie

data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)
