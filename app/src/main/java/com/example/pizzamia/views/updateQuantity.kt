package com.example.pizzamia.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pizzamia.network.RetrofitInstance
import com.example.pizzamia.network.AssignTagRequest
import com.example.pizzamia.network.InventoryUpdateApi
import com.example.pizzamia.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun UpdateQuantityScreen(onUnauthorized: () -> Unit, onLogout: () -> Unit) {
    var rfidTag by remember  { mutableStateOf("") }
    var newQuantity by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold (
        topBar = {
            AppBar(
                userName = SessionManager(context).getUserName() ?: "",
                onLogout = onLogout
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Actualizar Cantidad",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = rfidTag,
                onValueChange = { rfidTag = it },
                label = { Text("Etiqueta RFID") },
                placeholder = { Text("Escanea o ingresa la etiqueta") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newQuantity,
                onValueChange = { newQuantity = it },
                label = { Text("Nueva Cantidad") },
                placeholder = { Text("Ingresa la nueva cantidad") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button (
                onClick = {
                    if (rfidTag.isEmpty() || newQuantity.isEmpty()) {
                        Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    scope.launch {
                        try {
                            val token = SessionManager(context).getToken()
                            val api = RetrofitInstance(token, onUnauthorized).create(InventoryUpdateApi::class.java)
                            val response = api.updateQuantity(id = rfidTag,request=AssignTagRequest(
                                    id = null,
                                    cantidad = newQuantity.toDouble(),
                                    numParte = "",
                                    lote = "",
                                    Etiqueta = "",
                                    usuario = ""
                                )
                            )

                            if (response.isSuccessful) {
                                rfidTag = ""
                                newQuantity = ""
                                Toast.makeText(context, "Cantidad actualizada correctamente.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Ocurrió un error: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Actualizar")
            }
        }
    }
}
