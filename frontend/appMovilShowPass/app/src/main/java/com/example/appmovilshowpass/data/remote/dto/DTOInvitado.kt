package com.example.appmovilshowpass.data.remote.dto

data class DTOInvitado(
    val id: Long,
    val nombre: String? = null,
    val apellidos: String? = null,
    val fotoURL: String? = null,
    val descripcion: String? = null
)