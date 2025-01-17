package com.example.pizzamia.network


import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val data: objData
)

data class objData(
    val token: String,
    val name: String
)

interface LoginApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): retrofit2.Response<LoginResponse>
}