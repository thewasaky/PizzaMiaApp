package com.example.pizzamia.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pizzamia.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(userName: String, onLogout: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Logo a la izquierda
                Icon(
                    painter = painterResource(id = R.drawable.logopaginasandra), // Reemplaza con el ID de tu logo
                    contentDescription = "Logo",
                    tint = Color.Unspecified, // Mantén los colores originales del logo
                    modifier = Modifier
                        .size(72.dp) // Tamaño del logo
                        .padding(end = 8.dp) // Espaciado entre logo y texto
                )
                Text(
                    text = "Hola, $userName",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f) // Espacio flexible para centrar el contenido
                )
                IconButton(onClick = { onLogout() }) {
                    Image(
                        painter = painterResource(id = R.drawable.logout_24dp_e8eaed), // Reemplaza con el nombre del archivo
                        contentDescription = "Cerrar sesión",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

