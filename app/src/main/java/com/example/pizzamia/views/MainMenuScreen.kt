package com.example.pizzamia.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pizzamia.session.SessionManager

@Composable
fun MainMenu(onOptionSelected: (Int) -> Unit, onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            AppBar (userName = SessionManager(context = LocalContext.current).getUserName() ?: "", onLogout = onLogout)
        }
    ) { paddingValues ->
        val options = listOf(
            "Registrar Etiqueta",
            "Actualizar Cantidad",
            "Ubicar Etiqueta",
            "Escaneo Masivo",
            "Reasignar Etiqueta",
            "Imprimir Etiqueta",
            "Etiquetas Registradas"
            //"Cerrar Sesión"
        )
        val icons = listOf(
            Icons.Default.Add,
            Icons.Default.Edit,
            Icons.Default.LocationOn,
            Icons.Default.Search,
            Icons.Default.Info,
            Icons.Default.Send,
            Icons.Default.Menu
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Sigue usando 2 columnas
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(8.dp), // Reducimos un poco el padding general
            contentPadding = PaddingValues(4.dp) // Espaciado más pequeño entre los elementos
        ) {
            items(options.size) { index ->
                Card(
                    modifier = Modifier
                        .padding(4.dp) // Margen interno más pequeño
                        .fillMaxWidth()
                        .aspectRatio(1.2f), // Relación de aspecto más compacta
                    onClick = { onOptionSelected(index) },
                    elevation = CardDefaults.cardElevation(2.dp), // Reducción de la sombra para que sea más plano
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp) // Íconos más pequeños
                        )
                        Spacer(modifier = Modifier.height(4.dp)) // Espacio reducido entre el ícono y el texto
                        Text(
                            text = options[index],
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall // Fuente más pequeña
                        )
                    }
                }
            }
        }
    }
}
