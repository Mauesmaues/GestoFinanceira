package com.example.gestofinanceira.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestofinanceira.data.Entrada
import com.example.gestofinanceira.data.Saida
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BalanceScreen(viewModel: FinanceiroViewModel) {
    // Observar estados do ViewModel
    val saldoTotal by viewModel.saldoTotal.collectAsState()
    val saldoEmDolar by viewModel.saldoEmDolar.collectAsState()
    val todasEntradas by viewModel.todasEntradas.collectAsState(initial = emptyList())
    val todasSaidas by viewModel.todasSaidas.collectAsState(initial = emptyList())
    val cotacaoDolar by viewModel.cotacaoDolar.collectAsState()

    // Inputs como texto para facilitar edição
    var entradaText by remember { mutableStateOf(TextFieldValue("")) }
    var saidaText by remember { mutableStateOf(TextFieldValue("")) }

    var tabelaExibida by remember { mutableStateOf("receita") }

    // Buscar cotação do dólar ao iniciar
    LaunchedEffect(Unit) {
        viewModel.atualizarCotacaoDolar()
    }

    val formatoBR = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formatoUS = NumberFormat.getCurrencyInstance(Locale.US)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // DIV 1: Linha com dois cards mostrando saldo e saldo convertido em dólar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Saldo Atual",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatoBR.format(saldoTotal),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Saldo em USD",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatoUS.format(saldoEmDolar),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // DIV 2: Card com inputs formatados R$ e botões Entrada/Saída
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Linha 1: Input Entrada + Botão Entrada
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = entradaText,
                        onValueChange = { entradaText = it },
                        label = { Text("Valor Entrada (R$)") },
                        placeholder = { Text("0,00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val valor = parseCurrencyToDouble(entradaText.text)
                            if (valor > 0) {
                                viewModel.adicionarEntrada(
                                    valor = valor,
                                    descricao = "Entrada de ${formatoBR.format(valor)}"
                                )
                                entradaText = TextFieldValue("")
                            }
                        },
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Entrada")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Linha 2: Input Saída + Botão Saída
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = saidaText,
                        onValueChange = { saidaText = it },
                        label = { Text("Valor Saída (R$)") },
                        placeholder = { Text("0,00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val valor = parseCurrencyToDouble(saidaText.text)
                            if (valor > 0) {
                                viewModel.adicionarSaida(
                                    valor = valor,
                                    descricao = "Saída de ${formatoBR.format(valor)}"
                                )
                                saidaText = TextFieldValue("")
                            }
                        },
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Saída")
                    }
                }
            }
        }

        // DIV 3: Botões para alternar entre Receita e Despesa
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { tabelaExibida = "receita" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (tabelaExibida == "receita")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "Receitas",
                    color = if (tabelaExibida == "receita")
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { tabelaExibida = "despesa" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (tabelaExibida == "despesa")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "Despesas",
                    color = if (tabelaExibida == "despesa")
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // DIV 4: Tabela com LazyColumn para exibir itens
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (tabelaExibida == "receita") "Lista de Receitas" else "Lista de Despesas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val listaAtual = if (tabelaExibida == "receita") todasEntradas else todasSaidas

                if (listaAtual.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum item registrado",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (tabelaExibida == "receita") {
                            items(todasEntradas) { entrada ->
                                EntradaCard(entrada = entrada, formatoBR = formatoBR)
                            }
                        } else {
                            items(todasSaidas) { saida ->
                                SaidaCard(saida = saida, formatoBR = formatoBR)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EntradaCard(entrada: Entrada, formatoBR: NumberFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entrada.descricao,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "ID: ${entrada.id}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatoBR.format(entrada.valor),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SaidaCard(saida: Saida, formatoBR: NumberFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = saida.descricao,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "ID: ${saida.id}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatoBR.format(saida.valor),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Função utilitária simples para transformar entrada textual em Double (R$), aceita vírgula ou ponto
private fun parseCurrencyToDouble(text: String): Double {
    if (text.isBlank()) return 0.0
    // Remove tudo que não é dígito, vírgula ou ponto ou menos
    val cleaned = text.replace("[^0-9,.-]".toRegex(), "")
        .replace(',', '.')
    return try {
        cleaned.toDouble()
    } catch (e: Exception) {
        0.0
    }
}
