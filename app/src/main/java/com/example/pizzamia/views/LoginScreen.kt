package com.example.pizzamia.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzamia.network.LoginApi
import com.example.pizzamia.session.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import android.util.Base64
import com.example.pizzamia.network.LoginRequest
import com.example.pizzamia.R
import com.example.pizzamia.network.RetrofitInstance
import org.json.JSONObject


fun decodeJwt(token: String): JSONObject? {
    return try {
        val parts = token.split(".")
        if (parts.size < 2) return null
        val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
        JSONObject(payload)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(is401:Boolean=false,onLoginSuccess: () -> Unit,) {
    var username by remember { mutableStateOf("sandrahl") }
    var password by remember { mutableStateOf("220774") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    //sessionManager.clearLoginStatus()
    val save: () -> Unit = {}
    // Función para manejar el login
    fun performLogin() {
        scope.launch {
            try {
                // Crear el cuerpo de la solicitud
                val loginRequest = LoginRequest(username, password)
                val loginApi = RetrofitInstance("",save).create(LoginApi::class.java)
                val response = loginApi.login(loginRequest);
                // Verificar si la respuesta fue exitosa
                if (response.isSuccessful) {
                    val token = response.body()?.data?.token
                    val nombre=response.body()?.data?.name
                    if (token != null) {
                        // Guardar el token o realizar cualquier otra acción
                        if (nombre != null) {
                            sessionManager.saveLoginStatus(token,nombre)
                            Toast.makeText(context, "Bienvenido $nombre", Toast.LENGTH_LONG).show()
                            onLoginSuccess()
                        }else{
                            Toast.makeText(context, "Error: nombre no encontrado.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Error: token no encontrado.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Credenciales incorrectas.", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                Toast.makeText(context, "Ocurrió un error: ${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: HttpException) {
                Toast.makeText(context, "Error en el servidor: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Cargar datos iniciales
    LaunchedEffect(Unit) {
       if(is401){
           Toast.makeText(context, "Sesión expirada", Toast.LENGTH_LONG).show()
       }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo de la empresa (si está configurado)
        Image(
            painter = painterResource(id = R.drawable.logopaginasandra), // Reemplaza con el nombre real del logo
            contentDescription = "Logo de la empresa",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp)
        )

        // Título
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                color = Color(0xFF404B93) // Azul personalizado
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Usuario
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario", color = Color(0xFF939597)) }, // Gris personalizado
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF404B93),
                unfocusedBorderColor = Color(0xFF939597),
                focusedTextColor = Color(0xFF404B93),
                unfocusedTextColor = Color(0xFF404B93)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )

        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = Color(0xFF939597)) }, // Gris personalizado
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF404B93),
                unfocusedBorderColor = Color(0xFF939597),
                focusedTextColor = Color(0xFF404B93),
                unfocusedTextColor = Color(0xFF404B93)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,

            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Inicio de Sesión
        Button(
            onClick = { performLogin() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF404B93),
                contentColor = Color.White
            )
        ) {
            Text("Iniciar Sesión")
        }

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
