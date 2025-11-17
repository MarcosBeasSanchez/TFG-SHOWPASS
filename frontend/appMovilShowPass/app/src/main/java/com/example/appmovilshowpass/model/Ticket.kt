package com.example.appmovilshowpass.model

data class Ticket (
    val id: Long,
    val codigoQR: String,
    val fechaCompra: String,
    val precioPagado: Double,
    val estado: EstadoTicket,
    val usuarioId: Long,
    val eventoId: Long,
    val nombreEvento: String
)