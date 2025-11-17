package com.example.gestofinanceira.data.api

import com.google.gson.annotations.SerializedName

// Estrutura principal da resposta, ex: {"USDBRL": {...}}
data class CotacaoApiResponse(
    @SerializedName("USDBRL") // Mapeia o campo "USDBRL" do JSON para a propriedade 'cotacaoUsdBrl'
    val cotacaoUsdBrl: CotacaoData?
)

// Estrutura interna com o valor da cotação
data class CotacaoData(
    @SerializedName("bid") // Mapeia o campo "bid" (valor de compra) para a propriedade 'valor'
    val valor: String?
)
