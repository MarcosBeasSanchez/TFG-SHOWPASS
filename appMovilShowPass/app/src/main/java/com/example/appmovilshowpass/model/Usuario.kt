package com.example.appmovilshowpass.model

data class Usuario (
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: String,
    val foto: String? = null
)