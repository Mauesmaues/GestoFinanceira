package com.example.gestofinanceira.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gestofinanceira.data.Entrada
import com.example.gestofinanceira.data.Saida
import kotlinx.coroutines.flow.Flow

@Dao
interface TransacaoDao {
    // --- Operações para Entradas ---
    @Insert
    suspend fun inserirEntrada(entrada: Entrada)

    @Update
    suspend fun updateEntrada(entrada: Entrada)

    @Delete
    suspend fun deleteEntrada(entrada: Entrada)

    @Query("SELECT * FROM tabela_entradas ORDER BY data DESC")
    fun getTodasEntradas(): Flow<List<Entrada>>

    @Query("SELECT SUM(valor) FROM tabela_entradas")
    fun getSomaEntradas(): Flow<Double?>

    // --- Operações para Saídas ---
    @Insert
    suspend fun inserirSaida(saida: Saida)

    @Update
    suspend fun updateSaida(saida: Saida)

    @Delete
    suspend fun deleteSaida(saida: Saida)

    @Query("SELECT * FROM tabela_saidas ORDER BY data DESC")
    fun getTodasSaidas(): Flow<List<Saida>>

    @Query("SELECT SUM(valor) FROM tabela_saidas")
    fun getSomaSaidas(): Flow<Double?>
}
