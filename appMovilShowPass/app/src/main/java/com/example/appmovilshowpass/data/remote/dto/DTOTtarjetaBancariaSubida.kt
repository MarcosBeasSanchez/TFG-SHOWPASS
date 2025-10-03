package com.example.appmovilshowpass.data.remote.dto

import com.example.appmovilshowpass.model.TarjetaBancaria
import java.math.BigDecimal

data class DTOtarjetaBancariaSubida(
    val nombreTitular: String = "",
    val fechaCaducidad: String = "",
    val cvv: String = "",
    val saldo: BigDecimal = BigDecimal.ZERO,
    val nTarjeta: String = ""
)


fun TarjetaBancaria.toDTOsubida(): DTOtarjetaBancariaSubida {
    return DTOtarjetaBancariaSubida(
        nombreTitular = this.nombreTitular,
        fechaCaducidad = this.fechaCaducidad.toString(), // "YYYY-MM-DD"
        cvv = this.cvv,
        saldo = this.saldo,
        nTarjeta = this.nTarjeta
    )
}