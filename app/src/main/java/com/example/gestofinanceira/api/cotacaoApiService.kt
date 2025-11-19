package com.example.gestofinanceira.api

import retrofit2.Response
import retrofit2.http.GET

interface CotacaoApiService {
    // Exemplo de endpoint da AwesomeAPI para Dólar-Real
    @GET("json/last/USD-BRL")
    suspend fun getCotacaoDolarAtual(): Response<CotacaoApiResponse>
}
