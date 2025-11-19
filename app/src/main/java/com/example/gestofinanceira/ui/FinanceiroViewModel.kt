package com.example.gestofinanceira.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestofinanceira.repository.FinanceiroRepository
import com.example.gestofinanceira.data.Entrada
import com.example.gestofinanceira.data.Saida
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// O ViewModel agora recebe o Repository
class FinanceiroViewModel(private val repository: FinanceiroRepository) : ViewModel() {

    // Fluxos de dados vindo do repositório
    val todasEntradas = repository.todasEntradas
    val todasSaidas = repository.todasSaidas

    // Saldo Total em Reais
    val saldoTotal: StateFlow<Double> =
        combine(repository.somaEntradas, repository.somaSaidas) { somaEntradas, somaSaidas ->
            (somaEntradas ?: 0.0) - (somaSaidas ?: 0.0)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0.0
        )

    // --- Lógica da Cotação do Dólar ---

    // StateFlow para armazenar a cotação do dólar
    private val _cotacaoDolar = MutableStateFlow<Double?>(null)
    val cotacaoDolar: StateFlow<Double?> = _cotacaoDolar.asStateFlow()

    // StateFlow para armazenar o saldo convertido em dólar
    val saldoEmDolar: StateFlow<Double> =
        combine(saldoTotal, _cotacaoDolar) { saldo, cotacao ->
            if (cotacao != null && cotacao > 0) {
                saldo / cotacao
            } else {
                0.0 // Retorna 0 se a cotação não estiver disponível
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0.0
        )

    // Função para iniciar a busca da cotação
    fun atualizarCotacaoDolar() {
        viewModelScope.launch {
            _cotacaoDolar.value = repository.buscarCotacaoDolar()
        }
    }

    // --- Funções de Transação ---
    fun adicionarEntrada(valor: Double, descricao: String) {
        viewModelScope.launch {
            repository.inserirEntrada(Entrada(valor = valor, descricao = descricao))
        }
    }

    fun adicionarSaida(valor: Double, descricao: String) {
        viewModelScope.launch {
            repository.inserirSaida(Saida(valor = valor, descricao = descricao))
        }
    }
}