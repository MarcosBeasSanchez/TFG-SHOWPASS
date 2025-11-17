package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.EstadoTicket

data class DTOTicketBajada (
    val id: Long,
    val codigoQR: String,
    val fechaCompra: String,
    val precioPagado: Double,
    val estado: EstadoTicket,
    val usuarioId: Long,
    val nombreUsuario: String,
    val eventoId: Long,
    val nombreEvento: String
)