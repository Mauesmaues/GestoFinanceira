package com.example.gestofinanceira.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val saldoTotal by viewModel.saldoTotal.collectAsState()
    val saldoEmDolar by viewModel.saldoEmDolar.collectAsState()
    val todasEntradas by viewModel.todasEntradas.collectAsState(initial = emptyList())
    val todasSaidas by viewModel.todasSaidas.collectAsState(initial = emptyList())
    val cotacaoDolar by viewModel.cotacaoDolar.collectAsState()

    var entradaText by remember { mutableStateOf(TextFieldValue("")) }
    var saidaText by remember { mutableStateOf(TextFieldValue("")) }
    var tabelaExibida by remember { mutableStateOf("receita") }
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedEntrada by remember { mutableStateOf<Entrada?>(null) }
    var selectedSaida by remember { mutableStateOf<Saida?>(null) }

    LaunchedEffect(Unit) {
        viewModel.atualizarCotacaoDolar()
    }

    val formatoBR = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formatoUS = NumberFormat.getCurrencyInstance(Locale.US)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SaldoCard("Saldo Atual", formatoBR.format(saldoTotal), MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
            SaldoCard("Saldo em USD", formatoUS.format(saldoEmDolar), MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TransactionInputRow(
                    textValue = entradaText,
                    onValueChange = { entradaText = it },
                    label = "Valor Entrada (R$)",
                    buttonText = "Entrada",
                    onButtonClick = {
                        val valor = parseCurrencyToDouble(entradaText.text)
                        if (valor > 0) {
                            viewModel.adicionarEntrada(valor, "Entrada de ${'$'}{formatoBR.format(valor)}")
                            entradaText = TextFieldValue("")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                TransactionInputRow(
                    textValue = saidaText,
                    onValueChange = { saidaText = it },
                    label = "Valor Saída (R$)",
                    buttonText = "Saída",
                    onButtonClick = {
                        val valor = parseCurrencyToDouble(saidaText.text)
                        if (valor > 0) {
                            viewModel.adicionarSaida(valor, "Saída de ${'$'}{formatoBR.format(valor)}")
                            saidaText = TextFieldValue("")
                        }
                    },
                    buttonColors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterButton("Receitas", tabelaExibida == "receita") { tabelaExibida = "receita" }
            FilterButton("Despesas", tabelaExibida == "despesa") { tabelaExibida = "despesa" }
        }

        Button(
            onClick = {
                val saldoBRL = formatoBR.format(saldoTotal)
                val saldoUSD = formatoUS.format(saldoEmDolar)
                val cotacao = cotacaoDolar?.let { "Cotação do Dólar: R$ %.2f".format(it) } ?: "Cotação não disponível"

                val mensagem = """
                *Resumo Financeiro*

                *Saldo Atual:* $saldoBRL
                *Saldo em Dólar:* $saldoUSD
                _($cotacao)_
                """.trimIndent()

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, mensagem)
                }
                val chooser = Intent.createChooser(intent, "Compartilhar Saldo")
                context.startActivity(chooser)
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Compartilhar Saldo")
        }

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = if (tabelaExibida == "receita") "Lista de Receitas" else "Lista de Despesas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                if (tabelaExibida == "receita") {
                    items(todasEntradas) { entrada ->
                        EntradaCard(
                            entrada = entrada,
                            formatoBR = formatoBR,
                            onEdit = {
                                selectedEntrada = it
                                showEditDialog = true
                            },
                            onDelete = {
                                selectedEntrada = it
                                showDeleteDialog = true
                            }
                        )
                    }
                } else {
                    items(todasSaidas) { saida ->
                        SaidaCard(
                            saida = saida,
                            formatoBR = formatoBR,
                            onEdit = {
                                selectedSaida = it
                                showEditDialog = true
                            },
                            onDelete = {
                                selectedSaida = it
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        val itemToEdit = selectedEntrada ?: selectedSaida
        if (itemToEdit != null) {
            EditTransactionDialog(
                item = itemToEdit,
                onDismiss = {
                    showEditDialog = false
                    selectedEntrada = null
                    selectedSaida = null
                },
                onSave = {
                    if (it is Entrada) viewModel.updateEntrada(it)
                    if (it is Saida) viewModel.updateSaida(it)
                    showEditDialog = false
                    selectedEntrada = null
                    selectedSaida = null
                }
            )
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDismiss = {
                showDeleteDialog = false
                selectedEntrada = null
                selectedSaida = null
            },
            onConfirm = {
                selectedEntrada?.let { viewModel.deleteEntrada(it) }
                selectedSaida?.let { viewModel.deleteSaida(it) }
                showDeleteDialog = false
                selectedEntrada = null
                selectedSaida = null
            }
        )
    }
}

@Composable
fun RowScope.SaldoCard(title: String, amount: String, containerColor: Color, contentColor: Color) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = contentColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = amount, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}

@Composable
fun TransactionInputRow(
    textValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = textValue,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text("0,00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Button(
            onClick = onButtonClick,
            modifier = Modifier.height(56.dp),
            colors = buttonColors
        ) {
            Text(buttonText)
        }
    }
}

@Composable
fun RowScope.FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun EntradaCard(entrada: Entrada, formatoBR: NumberFormat, onEdit: (Entrada) -> Unit, onDelete: (Entrada) -> Unit) {
    TransactionCard(
        description = entrada.descricao,
        value = formatoBR.format(entrada.valor),
        onEdit = { onEdit(entrada) },
        onDelete = { onDelete(entrada) },
        valueColor = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SaidaCard(saida: Saida, formatoBR: NumberFormat, onEdit: (Saida) -> Unit, onDelete: (Saida) -> Unit) {
    TransactionCard(
        description = saida.descricao,
        value = formatoBR.format(saida.valor),
        onEdit = { onEdit(saida) },
        onDelete = { onDelete(saida) },
        valueColor = MaterialTheme.colorScheme.error
    )
}

@Composable
fun TransactionCard(description: String, value: String, onEdit: () -> Unit, onDelete: () -> Unit, valueColor: Color) {
    var showMenu by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Text(description, fontWeight = FontWeight.Medium)
                Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = valueColor)
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, onClick = {
                        onEdit()
                        showMenu = false
                    })
                    DropdownMenuItem(text = { Text("Excluir") }, onClick = {
                        onDelete()
                        showMenu = false
                    })
                }
            }
        }
    }
}

@Composable
fun EditTransactionDialog(item: Any, onDismiss: () -> Unit, onSave: (Any) -> Unit) {
    val isEntrada = item is Entrada
    val initialDescription = if (isEntrada) (item as Entrada).descricao else (item as Saida).descricao
    val initialAmount = (if (isEntrada) (item as Entrada).valor else (item as Saida).valor).toString()

    var description by remember { mutableStateOf(initialDescription) }
    var amount by remember { mutableStateOf(initialAmount) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Transação") },
        text = {
            Column {
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Valor") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            }
        },
        confirmButton = {
            Button(onClick = {
                val valor = parseCurrencyToDouble(amount)
                if (valor > 0) {
                    val updatedItem = if (isEntrada) {
                        (item as Entrada).copy(descricao = description, valor = valor)
                    } else {
                        (item as Saida).copy(descricao = description, valor = valor)
                    }
                    onSave(updatedItem)
                }
            }) {
                Text("Salvar")
            }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun DeleteConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Exclusão") },
        text = { Text("Você tem certeza que deseja excluir este item?") },
        confirmButton = { Button(onClick = onConfirm) { Text("Excluir") } },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun parseCurrencyToDouble(text: String): Double {
    if (text.isBlank()) return 0.0
    val cleaned = text.replace("[^0-9,.-]".toRegex(), "").replace(',', '.')
    return cleaned.toDoubleOrNull() ?: 0.0
}
