package com.example.gestofinanceira.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tabela_saidas")
data class Saida(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val valor: Double,
    val descricao: String,
    val data: Date = Date()
)
    