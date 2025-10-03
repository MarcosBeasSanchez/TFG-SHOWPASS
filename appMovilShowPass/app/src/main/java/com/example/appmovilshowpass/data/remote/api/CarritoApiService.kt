package com.example.appmovilshowpass.data.remote.api

import com.example.appmovilshowpass.data.remote.dto.DTOCarritoBajada
import com.example.appmovilshowpass.data.remote.dto.DTOEmailRequest
import retrofit2.http.*

interface CarritoApiService {

    @GET("tfg/carrito/{usuarioId}")
    suspend fun obtenerCarrito(@Path("usuarioId") usuarioId: Long): DTOCarritoBajada

    @POST("tfg/carrito/agregar/{usuarioId}/{eventoId}")
    suspend fun agregarEvento(
        @Path("usuarioId") usuarioId: Long,
        @Path("eventoId") eventoId: Long
    ): DTOCarritoBajada

    @DELETE("tfg/carrito/eliminar/{usuarioId}/{eventoId}")
    suspend fun eliminarEvento(
        @Path("usuarioId") usuarioId: Long,
        @Path("eventoId") eventoId: Long
    ): DTOCarritoBajada

    @DELETE("tfg/carrito/vaciar/{usuarioId}")
    suspend fun vaciarCarrito(@Path("usuarioId") usuarioId: Long): DTOCarritoBajada

    @GET("tfg/carrito/total/{usuarioId}")
    suspend fun calcularTotal(@Path("usuarioId") usuarioId: Long): Double

    @POST("tfg/carrito/finalizar/{usuarioId}")
    suspend fun finalizarCompra(@Path("usuarioId") usuarioId: Long): DTOCarritoBajada

    @POST("tfg/carrito/enviarPdfEmail")
    suspend fun enviarPdfEmail(@Body payload: Map<String, String>): String
}