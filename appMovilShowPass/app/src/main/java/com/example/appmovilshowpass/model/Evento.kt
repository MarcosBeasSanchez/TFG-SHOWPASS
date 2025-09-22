package com.example.appmovilshowpass.model



data class Evento(
    val id: Long,
    val nombre: String,
    val localizacion: String,
    val invitados: List<Invitado> = emptyList(),
    val imagen: String,
    val inicioEvento: String,      // mejor manejarlo como String y luego parsear a LocalDateTime
    val finEvento: String,
    val descripcion: String,
    val carrusels: List<String> = emptyList(),
    val precio: Double,
    val categoria: Categoria
)