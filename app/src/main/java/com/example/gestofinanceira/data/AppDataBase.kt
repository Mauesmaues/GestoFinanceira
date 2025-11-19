package com.example.gestofinanceira.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gestofinanceira.data.Entrada
import com.example.gestofinanceira.data.Saida

@Database(entities = [Entrada::class, Saida::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transacaoDao(): TransacaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestao_financeira_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}