package com.example.appmovilshowpass.data.remote.api

import com.example.appmovilshowpass.data.remote.dto.DTOeventoBajada
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.appmovilshowpass.data.local.BASE_URL //
import retrofit2.http.Query

interface EventoApiService {
    @GET("tfg/evento/findAll") // buscar todos los eventos
    suspend fun obtenerTodosEventos(): List<DTOeventoBajada>

    @GET("tfg/evento/filterByNombre") // buscar evento por nombre
    suspend fun obtenerEventoPorNombre(@Query("nombre") nombre: String): List<DTOeventoBajada>

    @GET("tfg/evento/findById") // buscar evento por id
    suspend fun findById(@Query("id") id: Long): DTOeventoBajada
}


object RetrofitClient {
    val eventoApiService: EventoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Usa la IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventoApiService::class.java)
    }
}