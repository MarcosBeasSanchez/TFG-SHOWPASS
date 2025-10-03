package com.example.appmovilshowpass.model

data class Carrito(
    val id: Long,
    val usuarioId: Long,
    val eventos: List<Evento> = emptyList()
)