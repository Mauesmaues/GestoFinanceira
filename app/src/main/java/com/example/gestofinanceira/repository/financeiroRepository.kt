package com.example.gestofinanceira.data

import com.example.gestofinanceira.data.api.CotacaoApiService
import kotlinx.coroutines.flow.Flow

class FinanceiroRepository(
    private val transacaoDao: TransacaoDao,
    private val apiService: CotacaoApiService
) {
    // --- Métodos do Banco de Dados Local ---
    val todasEntradas: Flow<List<Entrada>> = transacaoDao.getTodasEntradas()
    val todasSaidas: Flow<List<Saida>> = transacaoDao.getTodasSaidas()
    val somaEntradas: Flow<Double?> = transacaoDao.getSomaEntradas()
    val somaSaidas: Flow<Double?> = transacaoDao.getSomaSaidas()

    suspend fun inserirEntrada(entrada: Entrada) {
        transacaoDao.inserirEntrada(entrada)
    }

    suspend fun inserirSaida(saida: Saida) {
        transacaoDao.inserirSaida(saida)
    }

    // --- Método da API Externa ---
    suspend fun buscarCotacaoDolar(): Double? {
        return try {
            val response = apiService.getCotacaoDolarAtual()
            if (response.isSuccessful) {
                // Converte o valor de String para Double
                response.body()?.cotacaoUsdBrl?.valor?.toDoubleOrNull()
            } else {
                null // A chamada falhou
            }
        } catch (e: Exception) {
            // A chamada gerou uma exceção (ex: sem internet)
            null
        }
    }
}
