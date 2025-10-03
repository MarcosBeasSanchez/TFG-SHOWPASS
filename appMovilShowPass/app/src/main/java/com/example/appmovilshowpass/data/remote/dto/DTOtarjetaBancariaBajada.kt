package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.TarjetaBancaria
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDate

data class DTOtarjetaBancariaBajada(
    val id: Long = 0,
    val nombreTitular: String = "",
    val fechaCaducidad: String = "",
    val cvv: String = "",
    val saldo: BigDecimal = BigDecimal.ZERO,
    @SerializedName("ntarjeta")
    val nTarjeta: String = "",
)

fun DTOtarjetaBancariaBajada.toTarjetaBancaria(): TarjetaBancaria {
    return TarjetaBancaria(
        id = this.id,
        nombreTitular = this.nombreTitular ?: "",
        fechaCaducidad = if (this.fechaCaducidad.isNotBlank()) LocalDate.parse(this.fechaCaducidad) else LocalDate.now(),
        cvv = this.cvv ?: "",
        saldo = this.saldo ?: BigDecimal.ZERO,
        nTarjeta = this.nTarjeta ?: ""
    )

}