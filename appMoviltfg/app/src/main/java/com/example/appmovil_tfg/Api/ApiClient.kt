package com.example.appmovil_tfg.Api

import com.example.appmovil_tfg.Models.LoginRequest
import com.example.appmovil_tfg.Models.RegisterRequest
import com.example.appmovil_tfg.Models.UsuarioResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/tfg/usuario/login")
    suspend fun login(@Body request: LoginRequest): UsuarioResponse

    @POST("/tfg/usuario/register")
    suspend fun register(@Body request: RegisterRequest): UsuarioResponse
}

object ApiClient {
    private const val BASE_URL = "http://192.168.1.133:8080" // localhost en emulador

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}