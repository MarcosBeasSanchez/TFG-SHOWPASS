package com.example.appmovilshowpass.model

data class Register(
    val nombre: String,
    val email: String,
    val password: String,
    val fechaNacimiento: String, // formato "yyyy-MM-dd"
    val rol: String
)