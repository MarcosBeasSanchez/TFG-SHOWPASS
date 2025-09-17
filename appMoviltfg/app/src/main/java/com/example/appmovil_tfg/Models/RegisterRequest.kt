package com.example.appmovil_tfg.Models

data class RegisterRequest (
    val nombre: String,
    val email: String,
    val password: String,
    val fechaNacimiento: String
)