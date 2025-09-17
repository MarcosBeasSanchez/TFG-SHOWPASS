package com.example.appmovil_tfg.Models

data class UsuarioResponse (
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: String,
    val foto: String? = null
)