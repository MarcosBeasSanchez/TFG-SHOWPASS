package com.example.appmovilshowpass.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatearFecha(fecha: String, formato: String = "dd/MM/yyyy HH:mm"): String {
    return try {
        val parsed = LocalDateTime.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        parsed.format(DateTimeFormatter.ofPattern(formato))
    } catch (e: Exception) {
        fecha
    }
}