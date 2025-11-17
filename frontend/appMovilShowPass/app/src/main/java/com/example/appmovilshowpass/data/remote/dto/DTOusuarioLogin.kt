package com.example.appmovilshowpass.data.remote.dto

data class DTOusuarioLoginBajada (
    val exito : Boolean = false,
    val mensaje: String = "",
    val token: String = "",
    val dtousuarioBajada: DTOusuarioBajada? = null
)
