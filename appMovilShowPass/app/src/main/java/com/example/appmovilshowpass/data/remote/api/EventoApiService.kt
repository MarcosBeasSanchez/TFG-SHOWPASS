package com.example.appmovilshowpass.data.remote.api

import com.example.appmovilshowpass.data.remote.dto.DTOeventoBajada
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.appmovilshowpass.data.local.BASE_URL //
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioLoginBajada
import com.example.appmovilshowpass.model.Login
import com.example.appmovilshowpass.model.Register
import com.example.appmovilshowpass.model.Usuario
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventoApiService {
    @GET("tfg/evento/findAll") // buscar todos los eventos
    suspend fun obtenerTodosEventos(): List<DTOeventoBajada>

    @GET("tfg/evento/filterByNombre") // buscar evento por nombre
    suspend fun obtenerEventoPorNombre(@Query("nombre") nombre: String): List<DTOeventoBajada>

    @GET("tfg/evento/findById") // buscar evento por id
    suspend fun findById(@Query("id") id: Long): DTOeventoBajada

    @GET("tfg/evento/findByCategoria/{categoria}")
    suspend fun findByCategoria(@Path("categoria") categoria: String): List<DTOeventoBajada>

    @POST("/tfg/usuario/login")
    suspend fun login(@Body request: Login): DTOusuarioLoginBajada

    @POST("/tfg/usuario/register")
    suspend fun register(@Body request: Register): Usuario

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