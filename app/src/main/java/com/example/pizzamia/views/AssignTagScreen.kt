package com.example.pizzamia.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pizzamia.R
import com.example.pizzamia.network.RetrofitInstance
import com.example.pizzamia.network.AssignTagRequest
import com.example.pizzamia.network.InventoryApi
import com.example.pizzamia.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun AssignTagScreen(onUnauthorized: () -> Unit, onLogout: () -> Unit) {
    var rfidTag by remember { mutableStateOf("") }
    var partNumber by remember { mutableStateOf("") }
    var lotNumber by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold (
        topBar = {
            AppBar (
                userName = SessionManager(context).getUserName() ?: "",
                onLogout = onLogout
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Asegura que el contenido no se superponga con el TopBar
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registrar Etiqueta",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = rfidTag,
                onValueChange = { rfidTag = it },
                label = { Text("Etiqueta RFID") },
                placeholder = { Text("Escanea la etiqueta") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = partNumber,
                onValueChange = { partNumber = it },
                label = { Text("Número de Parte") },
                placeholder = { Text("Introduce el número de parte") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lotNumber,
                onValueChange = { lotNumber = it },
                label = { Text("Lote") },
                placeholder = { Text("Introduce el número de lote") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Cantidad") },
                placeholder = { Text("Introduce la cantidad") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (rfidTag.isEmpty() || partNumber.isEmpty() || lotNumber.isEmpty() || quantity.isEmpty()) {
                        Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    val assignTagRequest = AssignTagRequest(
                        numParte = partNumber,
                        lote = lotNumber,
                        cantidad = quantity.toDoubleOrNull() ?: 0.0,
                        Etiqueta = rfidTag,
                        id = null,
                        usuario = null
                    )

                    scope.launch {
                        try {
                            val token = SessionManager(context).getToken()
                            val api = RetrofitInstance(token, onUnauthorized).create(InventoryApi::class.java)
                            val response = api.assignTag(assignTagRequest)
                            if (response.isSuccessful) {
                                val isError = response.body()?.isError
                                if (isError == true) {
                                    Toast.makeText(context, "Error: ${response.body()?.mensaje}", Toast.LENGTH_LONG).show()
                                } else {
                                    //vaciar campos
                                    rfidTag = ""
                                    partNumber = ""
                                    lotNumber = ""
                                    quantity = ""
                                    Toast.makeText(context, "${response.body()?.mensaje}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Error en la respuesta: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Ocurrió un error: ${e.message}", Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.saveLabel)
                )
            }
        }
    }
}
