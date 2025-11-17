package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.CarritoItem

data class DTOCarritoItemSubida(
    val eventoId: Long,
    val cantidad: Int
)

fun CarritoItem.toDTOsubida(): DTOCarritoItemSubida {
    return DTOCarritoItemSubida(
        eventoId = eventoId,
        cantidad = cantidad
    )
}