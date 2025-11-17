package com.example.gestofinanceira

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestofinanceira.api.RetrofitClient
import com.example.gestofinanceira.data.AppDatabase
import com.example.gestofinanceira.data.FinanceiroRepository
import com.example.gestofinanceira.ui.FinanceiroScreen
import com.example.gestofinanceira.ui.FinanceiroViewModel
import com.example.gestofinanceira.ui.theme.GestãoFinanceiraTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceiroViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(this@MainActivity)
                val repository = FinanceiroRepository(database.transacaoDao(), RetrofitClient.instance)
                return FinanceiroViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestãoFinanceiraTheme {
                // Main screen of the financial management application
                FinanceiroScreen(viewModel = viewModel)
            }
        }
    }
}