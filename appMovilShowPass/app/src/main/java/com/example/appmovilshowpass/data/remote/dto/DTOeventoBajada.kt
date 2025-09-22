package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.model.Invitado

data class DTOeventoBajada(
    val id: Long? = null,
    val nombre: String? = null,
    val localizacion: String? = null,
    val inicioEvento: String? = null,   // LocalDateTime → lo tratamos como String
    val finEvento: String? = null,
    val invitados: List<DTOInvitado>? = emptyList(),
    val imagen: String? = null,
    val descripcion: String? = null,
    val carrusels: List<String>? = emptyList(),
    val precio: Double = 0.0,
    val categoria: Categoria? = Categoria.OTROS
)
//funciona para mappear un DTOeventoBajada a un Evento
fun DTOeventoBajada.toEvento(): Evento {
    return Evento(
        id = id ?: 0,
        nombre = nombre ?: "",
        localizacion = localizacion ?: "",
        inicioEvento = inicioEvento ?: "",
        finEvento = finEvento ?: "",
        invitados = invitados?.map {
            Invitado(
                nombre = it.nombre ?: "",
                apellidos = it.apellidos ?: "",
                fotoURL = it.fotoURL ?: "",
                descripcion = it.descripcion ?: ""
            )
        } ?: emptyList(),
        imagen = imagen ?: "",
        descripcion = descripcion ?: "",
        carrusels = carrusels ?: emptyList(),
        precio = precio,
        categoria = categoria ?: Categoria.OTROS
    )
}