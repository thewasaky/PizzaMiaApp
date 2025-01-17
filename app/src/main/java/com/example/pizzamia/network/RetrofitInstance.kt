package com.example.pizzamia.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class  RetrofitInstance (private val token: String?, private val onUnauthorized: () -> Unit ){
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val requestBuilder = chain.request().newBuilder()
            if (!token.isNullOrEmpty()) {
                // Agrega el encabezado solo si el token no está vacío
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
        // Interceptor para manejar errores 401
        .addInterceptor { chain: Interceptor.Chain ->
            val response: Response = chain.proceed(chain.request())
            if (response.code == 401) {
                CoroutineScope(Dispatchers.Main).launch {
                    onUnauthorized() // Llama la función para manejar 401
                }
                //cancelar el flujo de la petición
                return@addInterceptor response
            }
            response
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.pizzamia.eaproma.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}
