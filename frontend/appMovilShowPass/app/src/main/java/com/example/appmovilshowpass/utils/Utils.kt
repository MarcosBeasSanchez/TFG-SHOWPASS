package com.example.appmovilshowpass.utils

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Base64

import android.net.Uri
import android.util.Log

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter

import java.io.*
import java.net.URL

import com.example.appmovilshowpass.data.local.SERVER_BASE_URL_FOTOS
import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.lang.Exception

fun formatearFechayHora(fecha: String, formato: String = "dd/MM/yyyy HH:mm"): String {
    return try {
        val parsed = LocalDateTime.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        parsed.format(DateTimeFormatter.ofPattern(formato))
    } catch (e: Exception) {
        fecha
    }
}

fun formatearFecha(fecha: String, formato: String = "dd/MM/yyyy"): String {
    return try {
        val parsed = LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE)
        parsed.format(DateTimeFormatter.ofPattern(formato))
    } catch (e: Exception) {
        fecha
    }
}


fun formatearPrecio(precio: Double): String {
    return String.format("%.2f", precio)
}


/**
 * Genera un ticket en formato PDF
 * Devuelve el PDF en Base64 (útil para enviarlo por email).
 */
fun generarTicketPdf(
    context: Context,
    ticket: DTOTicketBajada,
    eventoFecha: String,
    eventoImagenUrl: String?
): String {
    val file = File(context.cacheDir, "ticket_${ticket.id}.pdf")
    val document = Document(PageSize.A5, 30f, 30f, 30f, 30f)
    PdfWriter.getInstance(document, FileOutputStream(file))
    document.open()

    // Colores
    val azulOscuro = BaseColor(0, 45, 98)
    val grisFondo = BaseColor(240, 240, 240)
    val amarilloTitulo = BaseColor(255, 204, 0)
    val grisTexto = BaseColor(120, 120, 120)

    // Fuentes
    val fontHeaderAzul = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD, azulOscuro)
    val fontEvento = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, amarilloTitulo )
    val fontTextoBlanco = Font(Font.FontFamily.HELVETICA, 7f, Font.BOLD, BaseColor.WHITE)
    val fontNegro = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, BaseColor.BLACK)
    val fontNegroBold = Font(Font.FontFamily.HELVETICA, 8f, Font.BOLD, BaseColor.BLACK)
    val fontGris = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, grisTexto)

    // ======= Encabezado con rectángulo gris =======
    val headTable = PdfPTable(1).apply { widthPercentage = 100f }
    val headCell = PdfPCell(Phrase("SHOWPASS", fontHeaderAzul)).apply {
        backgroundColor = grisFondo
        border = Rectangle.NO_BORDER
        setPadding(12f)
        horizontalAlignment = Element.ALIGN_LEFT
    }
    headTable.addCell(headCell)
    document.add(headTable)
    document.add(Paragraph("\n"))

    // ======= Bloque azul principal =======
    val tablaAzul = PdfPTable(floatArrayOf(1f, 2.3f, 1f)).apply {
        widthPercentage = 100f
        spacingBefore = 4f
        spacingAfter = 6f


        this.tableEvent = null
        this.defaultCell.cellEvent = null // Limpiar eventos que puedan añadir padding/spacing
        this.defaultCell.borderWidth = 0f // Asegurar que el borde no añada grosor
    }

    val alturaFila = 110f

    // Imagen del evento (izquierda)
    val imgCell = PdfPCell().apply {
        backgroundColor = azulOscuro
        border = Rectangle.NO_BORDER
        setPadding(2f)
        setFixedHeight(alturaFila)
        horizontalAlignment = Element.ALIGN_CENTER
        verticalAlignment = Element.ALIGN_MIDDLE
    }
    eventoImagenUrl?.let { url ->
        val fullUrl = construirUrlImagen(url)
        Log.d("TicketPDF", "Cargando imagen evento desde: $fullUrl")
        val img = cargarImagen(fullUrl)
        if (img != null) {
            img.scaleToFit(75f, 65f) // más pequeña para no salirse
            img.alignment = Element.ALIGN_CENTER
            imgCell.addElement(img)
        } else Log.e("TicketPDF", " Imagen evento no cargada")
    }
    tablaAzul.addCell(imgCell)

    // Texto del evento (centro)
    val infoEvento = Paragraph().apply {
        add(Chunk("${ticket.nombreEvento}\n", fontEvento))
        add(Chunk("Inicio del evento: ${formatearFechaFlexible(eventoFecha)}\n", fontTextoBlanco))
        add(Chunk("Fecha de compra: ${formatearFechaFlexible(ticket.fechaCompra)}\n", fontTextoBlanco))
        add(Chunk("Precio: ${"%.2f €".format(ticket.precioPagado)}", fontTextoBlanco))
        setLeading(18f, 0f) // interlineado
    }
    val infoCell = PdfPCell(infoEvento).apply {
        backgroundColor = azulOscuro
        border = Rectangle.NO_BORDER
        setPadding(2f)
        setFixedHeight(alturaFila)
        verticalAlignment = Element.ALIGN_MIDDLE
    }
    tablaAzul.addCell(infoCell)

    // QR (derecha)
    val qrCell = PdfPCell().apply {
        backgroundColor = azulOscuro
        border = Rectangle.NO_BORDER
        setPadding(2f)
        setFixedHeight(alturaFila)
        horizontalAlignment = Element.ALIGN_CENTER
        verticalAlignment = Element.ALIGN_MIDDLE
    }
    val qrUrl = construirUrlImagen(ticket.codigoQR)
    Log.d("TicketPDF", "🔳 Cargando QR desde: $qrUrl")
    val qrImg = cargarImagen(qrUrl)
    if (qrImg != null) {
        qrImg.scaleToFit(75f, 85f)
        qrImg.alignment = Element.ALIGN_CENTER
        qrCell.addElement(qrImg)
    } else Log.e("TicketPDF", "❌ QR no cargado")
    tablaAzul.addCell(qrCell)

    document.add(tablaAzul)

    // ======= Bloque gris inferior =======
    val parInfo = Paragraph().apply {
        add(Chunk("ID Ticket: ", fontNegroBold));   add(Chunk("${ticket.id}\n", fontNegro))
        add(Chunk("Usuario ID: ", fontNegroBold));  add(Chunk("${ticket.usuarioId}\n", fontNegro))
        add(Chunk("Evento ID: ", fontNegroBold));   add(Chunk("${ticket.eventoId}\n", fontNegro))
        add(Chunk("Nombre del evento: ", fontNegroBold)); add(Chunk("${ticket.nombreEvento}\n", fontNegro))
        add(Chunk("Inicio del evento: ", fontNegroBold)); add(Chunk("${formatearFechaFlexible(eventoFecha)}\n\n", fontNegro))
        add(Chunk("Prohibida la reventa. No reembolsable. Mostrar en la entrada del evento.", fontGris))
        setLeading(0f, 1.25f)
    }

    val celdaGris = PdfPCell(parInfo).apply {
        backgroundColor = grisFondo
        border = Rectangle.NO_BORDER
        setPadding(12f)
        setMinimumHeight(300f)
    }

    PdfPTable(1).apply {
        widthPercentage = 100f
        addCell(celdaGris)
        document.add(this)
    }

    document.close()
    val pdfBytes = file.readBytes()
    return Base64.encodeToString(pdfBytes, Base64.NO_WRAP)
}

/**
 * Carga imagen desde URL o Base64 (con logs).
 */
private fun cargarImagen(ruta: String?): Image? {
    if (ruta.isNullOrBlank()) return null
    return try {
        val bytes = when {
            ruta.startsWith("http") -> {
                runBlocking(Dispatchers.IO) {
                    try {
                        URL(ruta).openStream().use { it.readBytes() }
                    } catch (e: Exception) {
                        Log.e("TicketPDF", "Error al descargar imagen: ${e.message}")
                        ByteArray(0)
                    }
                }
            }
            ruta.startsWith("data:image") -> Base64.decode(ruta.substringAfter(","), Base64.DEFAULT)
            else -> Base64.decode(ruta, Base64.DEFAULT)
        }
        if (bytes.isNotEmpty()) Image.getInstance(bytes) else null
    } catch (e: Exception) {
        Log.e("TicketPDF", "Error cargando imagen: ${e.message}")
        null
    }
}

/**
 * Formatea fecha a dd/M/yyyy, HH:mm:ss
 */
private fun formatearFechaFlexible(fecha: String): String {
    val salida = DateTimeFormatter.ofPattern("d/M/yyyy, HH:mm:ss")
    val candidatos = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm"
    )
    for (pat in candidatos) {
        try {
            val entrada = DateTimeFormatter.ofPattern(pat)
            val parsed = LocalDateTime.parse(fecha.take(19).replace('T',' '), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]".replace("[:ss]", if (fecha.length>=19) ":ss" else "")))
            return parsed.format(salida)
        } catch (_: Exception) {}
        try {
            val entrada = DateTimeFormatter.ofPattern(pat)
            return LocalDateTime.parse(fecha.substring(0, minOf(fecha.length, pat.length)), entrada).format(salida)
        } catch (_: Exception) {}
    }
    // último intento: si viene como 2025-11-02T09:00
    return try {
        val entrada = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        LocalDateTime.parse(fecha.substring(0, 16), entrada).format(salida)
    } catch (_: Exception) {
        fecha // tal cual si no se pudo
    }
}


fun construirUrlImagen(ruta: String?): String {
    if (ruta.isNullOrBlank()) return ""

    return when {
        ruta.startsWith("http://") || ruta.startsWith("https://") ->
            ruta  // ya es URL válida

        ruta.startsWith("/uploads/") ->
            SERVER_BASE_URL_FOTOS + ruta // evitar doble slash

        ruta.startsWith("uploads/") ->
            "$SERVER_BASE_URL_FOTOS/$ruta"

        ruta.startsWith("data:image/") ->
            ruta  // Base64 completo, se usa tal cual

        // Base64 crudo → agregar cabecera
        else ->
            "data:image/png;base64,$ruta"
    }
}

fun imagenToBase64(context: Context, uri: Uri): String {
    val input = context.contentResolver.openInputStream(uri)
    val bytes = input?.readBytes() ?: return ""
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}