package com.example.appmovilshowpass.data.remote.api

import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
import com.example.appmovilshowpass.data.remote.dto.DTOTicketSubida
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TicketApiService {


    @GET("tfg/ticket/findByUsuarioId/{usuarioId}")
    suspend fun obtenerTicketsPorUsuario(@Path("usuarioId")usuarioId: Long): List<DTOTicketBajada>

    @POST("tfg/utilidades/enviarPdfEmail")
    suspend fun enviarPdfEmail(@Body body: Map<String, String>): Response<Map<String, String>>


    @DELETE("tfg/ticket/delete/all/{usuarioId}")
    suspend fun  eliminarTodosLosTickets(@Path("usuarioId") usuarioId: Long): Response<Map<String, String>>

    @DELETE("tfg/ticket/delete/{id}")
    suspend fun eliminarTicket(@Path("id") id: Long): Response<Map<String, Any>>

    // Valida el ticket por el campo contenidoQR
    @GET("tfg/ticket/validarQR")
    suspend fun validarCodigoQR(@Query("contenidoQR") codigoQR: String): Response<Boolean>

}