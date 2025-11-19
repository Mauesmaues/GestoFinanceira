package com.example.gestofinanceira.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {    // URL base da API que você vai usar. Ex: AwesomeAPI
    private const val BASE_URL = "https://economia.awesomeapi.com.br/"

    val instance: CotacaoApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(CotacaoApiService::class.java)
    }
}
