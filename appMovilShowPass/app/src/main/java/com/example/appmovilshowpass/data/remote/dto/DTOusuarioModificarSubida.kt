package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.Rol
import com.example.appmovilshowpass.model.Usuario
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class DTOusuarioModificarSubida (
    val id: Long = 0,
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val fechaNacimiento: String = "",
    val foto: String = "",
    val rol: Rol,
    @SerializedName("cuenta") // El campo en el JSON es "cuenta"
    val cuenta: DTOtarjetaBancariaSubida? = null,
    val activo: Boolean = true,
    val reportado: Boolean = false
)

