package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.Rol
import com.example.appmovilshowpass.model.Usuario
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class DTOusuarioBajada(
    val id: Long = 0,
    val nombre: String = "",
    val email: String = "",
    val fechaNacimiento: String = "",
    val foto: String = "",
    val rol: Rol = Rol.CLIENTE,
    @SerializedName("cuenta") // El campo en el JSON es "cuenta"
    val dtoTarjetaBancariaBajada: DTOtarjetaBancariaBajada? = null,
    val activo: Boolean = true
)

fun DTOusuarioBajada.toUsuario(): Usuario {
    return Usuario(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        fechaNacimiento = LocalDate.parse(this.fechaNacimiento),
        foto = this.foto ?: "",
        rol = this.rol,
        cuenta = this.dtoTarjetaBancariaBajada?.toTarjetaBancaria(),
        activo = this.activo
    )
}