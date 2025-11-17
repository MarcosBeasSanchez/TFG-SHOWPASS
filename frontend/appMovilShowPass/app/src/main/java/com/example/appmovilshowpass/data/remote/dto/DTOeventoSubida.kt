package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.model.Invitado
import com.example.appmovilshowpass.utils.construirUrlImagen

data class DTOeventoSubida(
    val id: Long? = null,
    val nombre: String,
    val localizacion: String,
    val inicioEvento: String,
    val finEvento: String,
    val descripcion: String,
    val precio: Double,
    val categoria: Categoria,
    val aforoMax: Int,
    val vendedorId: Long,
    val imagen: String?,
    val imagenesCarruselUrls: List<String>?,
    val invitados: List<DTOInvitadoSubida>?

    )
