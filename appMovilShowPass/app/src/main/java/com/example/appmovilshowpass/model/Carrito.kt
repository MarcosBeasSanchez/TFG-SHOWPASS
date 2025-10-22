package com.example.appmovilshowpass.model

data class Carrito(
    val id: Long,
    val estado: EstadoCarrito? = EstadoCarrito.ACTIVO,
    val items: List<CarritoItem> = emptyList()
)