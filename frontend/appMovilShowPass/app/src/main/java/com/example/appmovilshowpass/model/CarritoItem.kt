package com.example.appmovilshowpass.model

data class CarritoItem(
    val id: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val eventoId: Long,
    val nombreEvento: String,
    val imagenEvento: String? = null

)