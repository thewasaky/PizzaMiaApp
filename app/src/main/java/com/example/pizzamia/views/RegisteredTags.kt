package com.example.pizzamia.views
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pizzamia.network.RetrofitInstance
import com.example.pizzamia.network.AssignTagRequest
import com.example.pizzamia.network.InventoryListApi
import com.example.pizzamia.network.InventorySendEmailApi
import com.example.pizzamia.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun RegisteredTagsScreen(onUnauthorized: () -> Unit, onLogout: () -> Unit) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var tags by remember { mutableStateOf(listOf<AssignTagRequest>()) }
        var isLoading by remember { mutableStateOf(false) }
        var totalRecords by remember { mutableStateOf(0) }
        var offset by remember { mutableStateOf(0) }
        val limit = 5 // Tamaño de página

        // Estado del LazyList
        val listState = rememberLazyListState()
    // Función para cargar más datos
    fun loadMoreTags() {
        if (isLoading || (tags.size >= totalRecords && totalRecords != 0)) {
            // Si ya está cargando o todos los datos han sido cargados
            return
        }

        isLoading = true
        scope.launch {
            try {
                val token = SessionManager(context).getToken()
                val api = RetrofitInstance(token, onUnauthorized).create(InventoryListApi::class.java)
                val response = api.getRegisteredTags(offset, limit)

                if (response.isSuccessful) {
                    response.body()?.data?.let { result ->
                        tags = tags + result.Data // Agregar nuevos datos
                        totalRecords = result.TotalRecords // Actualizar total
                        offset += limit // Incrementar el offset
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }
        // Cargar datos iniciales
        LaunchedEffect(Unit) {
            loadMoreTags()
        }

        // Detectar cuando el usuario llega al final de la lista
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastVisibleIndex ->
                    if (lastVisibleIndex != null && lastVisibleIndex >= tags.size - 1 && !isLoading) {
                        // Si quedan menos de 5 elementos visibles, carga más datos
                        loadMoreTags()
                    }
                }
        }

    Scaffold(
        topBar = {
            AppBar(
                userName = SessionManager(context).getUserName() ?: "",
                onLogout = onLogout
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { loadMoreTags() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Botón "Enviar a mi correo"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            val token = SessionManager(context).getToken()
                            val api = RetrofitInstance(token, onUnauthorized).create(
                                InventorySendEmailApi::class.java
                            )
                            val result = api.sendEmail()
                            if (result.isSuccessful) {
                                if (result.body()?.isError == true) {
                                    Toast.makeText(
                                        context,
                                        "Error: ${result.body()?.mensaje}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(context, "Correo enviado", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                ) {
                    Text("Enviar a mi correo")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Listado de etiquetas
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 16.dp, bottom = 65.dp, end = 16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(tags) { tag ->
                    // Renderizar cada elemento
                    TagItem(tag = tag)
                }

                // Mostrar indicador de carga al final
                if (isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                }
            }
        }
    }



}
@Composable
fun TagItem(tag: AssignTagRequest) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary)
            .padding(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ){
            Text("Etiqueta RFID: ${tag.Etiqueta}", style = MaterialTheme.typography.bodyMedium)

            Text("ID: ${tag.id}", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)

        }
        Text("Número de Parte: ${tag.numParte}", style = MaterialTheme.typography.bodySmall)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ){
            Text("Lote: ${tag.lote}", style = MaterialTheme.typography.bodySmall)
            Text("Cantidad: ${tag.cantidad}", style = MaterialTheme.typography.bodySmall)

        }
    }
}
