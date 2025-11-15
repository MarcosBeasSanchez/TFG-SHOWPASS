package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.Carrito
import com.example.appmovilshowpass.model.EstadoCarrito

data class DTOCarritoBajada(
    val id: Long,
    val estado: EstadoCarrito,
    val items: List<DTOCarritoItemBajada>
)


fun DTOCarritoBajada.toCarrito(): Carrito {
    return Carrito(
        id = id,
        estado = estado,
        items = items.map { it.toCarritoItem() }
    )
}