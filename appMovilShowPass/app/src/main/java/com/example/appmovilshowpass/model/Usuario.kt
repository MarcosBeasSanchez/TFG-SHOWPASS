package com.example.appmovilshowpass.model

import java.time.LocalDate

data class Usuario (
    val id: Long,
    val nombre: String,
    val email: String,
    val password: String= "",
    val fechaNacimiento: LocalDate,
    val rol: Rol,
    val foto: String,
    val cuenta: TarjetaBancaria? = null,
    val activo: Boolean,
    val reportado: Boolean = false
)