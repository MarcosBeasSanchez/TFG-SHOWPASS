package com.example.appmovilshowpass.utils

import android.content.Context
import android.graphics.pdf.PdfDocument
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

fun formatearFecha(fecha: String, formato: String = "dd/MM/yyyy HH:mm"): String {
    return try {
        val parsed = LocalDateTime.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        parsed.format(DateTimeFormatter.ofPattern(formato))
    } catch (e: Exception) {
        fecha
    }
}

/**
 * Genera un PDF simple con la info de un ticket y lo devuelve como Base64 para enviar al backend.
 */
fun generarTicketPdf(context: Context, eventoNombre: String, ticketId: String): String {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    val paint = android.graphics.Paint()
    paint.textSize = 16f

    canvas.drawText("Ticket para: $eventoNombre", 80f, 100f, paint)
    canvas.drawText("ID Ticket: $ticketId", 80f, 140f, paint)

    pdfDocument.finishPage(page)

    // Guardamos en memoria temporal
    val outputStream = ByteArrayOutputStream()
    pdfDocument.writeTo(outputStream)
    pdfDocument.close()

    // Convertimos a Base64
    return Base64.getEncoder().encodeToString(outputStream.toByteArray())
}