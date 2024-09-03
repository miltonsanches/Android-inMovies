package br.com.mleslie.inmovies.network

import br.com.mleslie.inmovies.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${Constants.BEARER}")
                        .addHeader("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .build()
        )
        .build()

    val genreDbService: GenreDbService by lazy {
        retrofit.create(GenreDbService::class.java)
    }

    val movieDbService: MovieDbService by lazy {
        retrofit.create(MovieDbService::class.java)
    }
}