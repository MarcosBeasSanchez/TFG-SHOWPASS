package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.Evento

data class DTOeventoBajadaCarrito(
    val id: Long,
    val nombre: String,
    val imagen: String,
    val precio: Double
)


fun DTOeventoBajadaCarrito.toEvento(): Evento {
    return Evento(
        id = id,
        nombre = nombre,
        localizacion = "",
        invitados = emptyList(),
        imagen = imagen,
        inicioEvento = "",
        finEvento = "",
        descripcion = "",
        carrusels = emptyList(),
        precio = precio,
        categoria = com.example.appmovilshowpass.model.Categoria.OTROS // o el default que uses
    )
}
