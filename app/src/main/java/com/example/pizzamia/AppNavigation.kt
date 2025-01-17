package com.example.pizzamia

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzamia.session.SessionManager
import com.example.pizzamia.views.AssignTagScreen
import com.example.pizzamia.views.LoginScreen
import com.example.pizzamia.views.MainMenu
import com.example.pizzamia.views.ReassignTagScreen
import com.example.pizzamia.views.RegisteredTagsScreen
import com.example.pizzamia.views.UpdateQuantityScreen

@Composable
fun AppNavigation() {
    // Creamos el controlador de navegación
    val navController = rememberNavController()
    val sessionManager = SessionManager(context = LocalContext.current)
    val context = LocalContext.current
    val isLoggedIn = sessionManager.isLoggedIn()
    val is401 = remember { mutableStateOf(false) }
    // Redirigir a "login" si la sesión expira
    val onUnauthorized: () -> Unit = {
        sessionManager.clearLoginStatus()
        is401.value = true
        Toast.makeText(context, "Sesión expirada", Toast.LENGTH_SHORT).show()
        navController.navigate("login") {
            popUpTo("menu") { inclusive = true }
        }
    }
    val onLogout: () -> Unit = {
        sessionManager.clearLoginStatus()
        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        navController.navigate("login") {
            popUpTo("menu") { inclusive = true }
        }
    }
    // Si el usuario está autenticado, redirigir al menú
    val startDestination = if (isLoggedIn) "menu" else "login"
    // Cargar datos iniciales
    LaunchedEffect(Unit) {
        if(SessionManager(context).isTokenExpired()){
            onUnauthorized()
        }
    }
    // Definimos el NavHost que maneja las rutas de las pantallas
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            // Cuando se navega a "login", mostramos la pantalla de login
            LoginScreen (is401.value){
                is401.value = false
                navController.navigate("menu") {
                    // Limpiar la pila para evitar volver atrás al login
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        composable("menu") {
            // Cuando se navega a "menu", mostramos el menú principal
            MainMenu ( { selectedOption ->
                // Aquí gestionamos las opciones del menú
                println("Opción seleccionada: $selectedOption")
                when (selectedOption) {
                    0 -> navController.navigate("assignTag")
                    1 -> navController.navigate("updateQuantity")
                    2 -> navController.navigate("locateTag")
                    3 -> navController.navigate("massScan")
                    4 -> navController.navigate("reassignTag")
                    5 -> navController.navigate("printTag")
                    6 -> navController.navigate("tagList")
                }
            },onLogout=onLogout)
        }
        composable("assignTag") {
            // Aquí iría la pantalla para asignar etiqueta
            AssignTagScreen(onUnauthorized = onUnauthorized, onLogout = onLogout)
        }
        composable("updateQuantity") {
            // Aquí iría la pantalla para actualizar cantidad
            UpdateQuantityScreen (onUnauthorized = onUnauthorized, onLogout = onLogout)
        }
        composable("locateTag") {
            // Aquí iría la pantalla para ubicar etiqueta
        }
        composable("massScan") {
            // Aquí iría la pantalla para escaneo masivo
        }
        composable("reassignTag") {
            // Aquí iría la pantalla para reasignar etiqueta
            ReassignTagScreen (onUnauthorized = onUnauthorized, onLogout = onLogout)
        }
        composable("printTag") {
            // Aquí iría la pantalla para imprimir etiqueta
        }
        composable("tagList") {
            // Aquí iría la pantalla para etiquetas registradas
            RegisteredTagsScreen (onUnauthorized = onUnauthorized, onLogout = onLogout)
        }
    }

}

