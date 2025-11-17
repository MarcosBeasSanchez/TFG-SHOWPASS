package com.example.appmovilshowpass.data.remote.api

import com.example.appmovilshowpass.data.remote.dto.DTOCarritoBajada
import com.example.appmovilshowpass.data.remote.dto.DTOCarritoItemSubida
import com.example.appmovilshowpass.data.remote.dto.DTOEmailRequest
import retrofit2.Response
import retrofit2.http.*

interface CarritoApiService {

    @GET("tfg/carrito/{usuarioId}")
    suspend fun obtenerCarrito(@Path("usuarioId") usuarioId: Long): DTOCarritoBajada

    @POST("tfg/carrito/item/{usuarioId}/{eventoId}")
    suspend fun agregarItem(
        @Path("usuarioId") usuarioId: Long,
        @Path("eventoId") eventoId: Long,
        @Body body: Map<String, Int>
    ): DTOCarritoBajada

    @DELETE("tfg/carrito/itemEliminar/{usuarioId}/{itemId}")
    suspend fun eliminarItem(
        @Path("usuarioId") usuarioId: Long,
        @Path("itemId") itemId: Long
    ): DTOCarritoBajada

    @DELETE("tfg/carrito/vaciar/{usuarioId}")
    suspend fun vaciarCarrito(@Path("usuarioId") usuarioId: Long): Response<Unit>

    @POST("tfg/carrito/finalizar/{usuarioId}")
    suspend fun finalizarCompra(@Path("usuarioId") usuarioId: Long): Response<Unit>
}