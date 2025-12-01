package com.example.appmovilshowpass.data.remote.api

import com.example.appmovilshowpass.data.remote.dto.DTOEventoRecomendado
import com.example.appmovilshowpass.data.remote.dto.DTOeventoBajada
import retrofit2.http.GET
import com.example.appmovilshowpass.data.remote.dto.DTOUsuarioReportado
import com.example.appmovilshowpass.data.remote.dto.DTOeventoSubida
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioLoginBajada
import com.example.appmovilshowpass.data.remote.dto.DTOusuarioModificarSubida
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

    @GET("tfg/evento/filterByBusqueda") // buscar evento por nombre
    suspend fun obtenerEventoPorNombre(@Query("nombre") nombre: String): List<DTOeventoBajada>

    @GET("tfg/evento/findById") // buscar evento por id
    suspend fun findById(@Query("id") id: Long): DTOeventoBajada

    @GET("tfg/evento/findByCategoria/{categoria}")
    suspend fun findByCategoria(@Path("categoria") categoria: String): List<DTOeventoBajada>

    @DELETE("tfg/evento/delete/{id}")
    suspend fun deleteEvento(@Path("id") id: Long): Response<Unit>

    @POST("tfg/evento/insert/mobile")
    suspend fun crearEvento(@Body evento: DTOeventoSubida): DTOeventoBajada

    @GET("tfg/evento/findByVendedor/{idVendedor}")
    suspend fun getEventosByVendedor(@Path("idVendedor") id: Long): List<DTOeventoBajada>

    @PUT("tfg/evento/updateMovil/{id}")
    suspend fun actualizarEvento(
        @Path("id") id: Long,
        @Body dto: DTOeventoSubida
    ): DTOeventoBajada

    @DELETE("tfg/evento/deleteInvitado/{id}")
    suspend fun eliminarInvitado(@Path("id") id: Long)

    @GET("tfg/evento/recomendacionUsuario/{userId}")
    suspend fun obtenerRecomendacioPorUsuario(@Path("userId") id: Long): List<DTOEventoRecomendado>

    @GET("tfg/evento/recomendacionEvento/{eventoId}")
    suspend fun obtenerRecomendacioPorEvento(@Path("eventoId") id: Long): List<DTOEventoRecomendado>

}


