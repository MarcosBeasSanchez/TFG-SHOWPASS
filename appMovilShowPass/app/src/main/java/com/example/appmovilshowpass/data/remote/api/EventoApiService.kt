package com.example.appmovilshowpass.data.remote.api

import com.example.appmovilshowpass.data.remote.dto.DTOeventoBajada
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.appmovilshowpass.data.local.BASE_URL //
import com.example.appmovilshowpass.data.remote.dto.DTOUsuarioReportado
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioLoginBajada
import com.example.appmovilshowpass.model.Login
import com.example.appmovilshowpass.model.Register
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
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
    suspend fun register(@Body request: Register): DTOusuarioBajada

    @POST("/tfg/usuario/update/{id}")
    suspend fun updateUser(@Body usuario: DTOusuarioBajada, @Path("id") id: Long): DTOusuarioBajada

    @PUT("tfg/usuario/reportar")
    suspend fun reportUser(@Query("email") email: String): DTOUsuarioReportado

    @GET("tfg/usuario/findByEmail")
    suspend fun findUserByEmail(@Query("email") email: String): DTOUsuarioReportado

    @GET("tfg/usuario/findAllReportados")
    suspend fun findAllReportados(): List<DTOUsuarioReportado>

    @DELETE("tfg/evento/delete/{id}")
    suspend fun deleteEvento(@Path("id") id: Long): Response<Unit>

    @PUT("tfg/usuario/quitarReport")
    suspend fun removeReport(@Query("email") email: String): DTOUsuarioReportado
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