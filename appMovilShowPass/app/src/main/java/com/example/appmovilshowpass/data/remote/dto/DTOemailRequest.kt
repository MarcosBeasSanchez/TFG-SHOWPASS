package com.example.appmovilshowpass.data.remote.dto

data class DTOEmailRequest(
    val email: String,
    val ticketId: String,
    val eventoNombre: String,
    val pdfBase64: String
)