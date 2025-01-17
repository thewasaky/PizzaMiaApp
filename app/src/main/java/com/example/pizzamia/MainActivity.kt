package com.example.pizzamia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pizzamia.ui.theme.InventariosPielesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InventariosPielesTheme {
                // Aseguramos que la interfaz usa el NavController para manejar la navegación
                AppNavigation()

            }
        }
    }
}
