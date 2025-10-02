package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.Carrito

data class DTOCarritoBajada(
    val id: Long,
    val usuarioId: Long,
    val eventos: List<DTOeventoBajadaCarrito>
)


fun DTOCarritoBajada.toCarrito(): Carrito {
    return Carrito(
        id = id,
        usuarioId = usuarioId,
        eventos = eventos.map { it.toEvento() }
    )
}