package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.CarritoItem

data class DTOCarritoItemBajada(
    val id: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val eventoId: Long,
    val nombreEvento: String,
    val imagenEvento: String? = null
)

fun DTOCarritoItemBajada.toCarritoItem(): CarritoItem {
    return CarritoItem(
        id = id,
        cantidad = cantidad,
        precioUnitario = precioUnitario,
        eventoId = eventoId,
        nombreEvento = nombreEvento,
        imagenEvento = imagenEvento

    )
}