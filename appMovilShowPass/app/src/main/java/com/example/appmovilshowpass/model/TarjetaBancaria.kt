package com.example.appmovilshowpass.model

import java.math.BigDecimal
import java.time.LocalDate

data class TarjetaBancaria(
    val id: Long = 0,
    val nombreTitular: String = "",
    val fechaCaducidad: LocalDate,
    val cvv: String = "",
    val saldo: BigDecimal,
    val ntarjeta: String = "",

    )