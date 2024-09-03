package br.com.mleslie.inmovies.model

import br.com.mleslie.inmovies.data.GenreDB

data class GenreResponse(
    val genres: List<GenreDB>
)