package com.example.appmovilshowpass.data.remote.dto

data class DTOTicketBajada (
    val id: Long,
    val usuarioId: Long,
    val eventoId: Long,
    val codigoQR: String,
    val fechaCompra: String,
    val precio: Double,
    val eventoNombre: String,
    val eventoImagen: String?,
    val eventoInicio: String
)