package com.example.pizzamia.session

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // Clave para verificar el estado de autenticación
    private val TOKEN = "token" ;
    private val USERNAME = "username" ;
    // Guardar el estado de sesión
    fun saveLoginStatus(token:String, username:String) {
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN, token)
        editor.putString(USERNAME, username)
        editor.apply()
    }

    // Verificar si el usuario está autenticado
    fun isLoggedIn(): Boolean {
        val token= sharedPreferences.getString(TOKEN, null)
        return token != null
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN, null)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(USERNAME, null)
    }

    fun isTokenExpired(): Boolean {
        val token = sharedPreferences.getString(TOKEN, null) ?: ""
        if(token.isEmpty()) return true
        val exp = decodeJwtExp(token) ?: ""
        val currentTime = System.currentTimeMillis() / 1000
        return exp.toLong() < currentTime

    }

    private fun decodeJwtExp(token: String): String? {
        val decodedPayload = com.example.pizzamia.views.decodeJwt(token)
        val exp = decodedPayload?.optString("exp")
        return exp
    }

    fun clearLoginStatus() {
        val editor = sharedPreferences.edit()
        editor.remove(TOKEN)
        editor.apply()
    }


}
