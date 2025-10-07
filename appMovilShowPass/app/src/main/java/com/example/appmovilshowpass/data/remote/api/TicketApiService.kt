package com.example.appmovilshowpass.data.remote.api

import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
import com.example.appmovilshowpass.data.remote.dto.DTOTicketSubida
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TicketApiService {


    @GET("tfg/ticket/findByUsuarioId/{usuarioId}")
    suspend fun obtenerTicketsPorUsuario(@Path("usuarioId")usuarioId: Long): List<DTOTicketBajada>

    @POST("tfg/carrito/enviarPdfEmail")
    suspend fun enviarPdfEmail(@Body body: Map<String, String>): Response<Map<String, String>>

    @POST("tfg/ticket/insert")
    suspend fun insertarTicket(@Body dto: DTOTicketSubida): DTOTicketBajada
}