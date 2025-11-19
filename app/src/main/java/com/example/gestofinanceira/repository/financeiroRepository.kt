package com.example.gestofinanceira.repository

import android.util.Log
import com.example.gestofinanceira.api.CotacaoApiService
import com.example.gestofinanceira.data.Entrada
import com.example.gestofinanceira.data.Saida
import com.example.gestofinanceira.data.TransacaoDao
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
            Log.e("FinanceiroRepository", "Erro ao buscar cotação do dólar", e)
            // A chamada gerou uma exceção (ex: sem internet)
            null
        }
    }
}