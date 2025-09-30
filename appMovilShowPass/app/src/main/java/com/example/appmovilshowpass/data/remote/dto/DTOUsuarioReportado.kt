package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.TarjetaBancaria
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDate

data class DTOUsuarioReportado(
    val id: Long = 0,
    val nombre: String = "",
    val email: String = "",
    val reportado: Boolean = false,
)

fun DTOUsuarioReportado.toUsuarioReportado(): DTOUsuarioReportado {
    return DTOUsuarioReportado(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        reportado = this.reportado
    )
}