package com.example.appmovilshowpass.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Base64

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.NumberFormat
import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.intl.Locale
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.*
import java.net.URL
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.example.appmovilshowpass.data.local.SERVER_BASE_URL_FOTOS
import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.util.Calendar

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
    val document = Document(PageSize.A5)
    val writer = PdfWriter.getInstance(document, FileOutputStream(file))
    document.open()

    val azul = BaseColor(0, 45, 98)
    val grisFondo = BaseColor(245, 245, 245)
    val fontTitulo = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD, azul)
    val fontEvento = Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD, BaseColor.YELLOW)
    val fontTexto = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.WHITE)
    val fontTextoNegro = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)
    val fontPeque = Font(Font.FontFamily.HELVETICA, 8f, Font.ITALIC, BaseColor.GRAY)

    val tableMain = PdfPTable(1)
    tableMain.widthPercentage = 100f

    // ---Encabezado SHOWPASS ---
    val header = PdfPCell(Phrase("SHOWPASS", fontTitulo))
    header.backgroundColor = BaseColor(230, 236, 247)
    header.horizontalAlignment = Element.ALIGN_LEFT
    header.border = Rectangle.NO_BORDER
    header.paddingTop = 10f
    header.paddingBottom = 10f

    tableMain.addCell(header)

    // ---Sección principal azul ---
    val tableEvent = PdfPTable(floatArrayOf(1f, 2f, 1f))
    tableEvent.widthPercentage = 100f

    val cellEventBg = PdfPCell()
    cellEventBg.backgroundColor = azul
    cellEventBg.colspan = 3
    cellEventBg.border = Rectangle.NO_BORDER
    cellEventBg.paddingTop = 8f
    cellEventBg.paddingBottom = 8f


    // Imagen del evento
    val imgCell = PdfPCell()
    imgCell.border = Rectangle.NO_BORDER
    imgCell.backgroundColor = azul
    eventoImagenUrl?.let { url ->
        val img = cargarImagen(construirUrlImagen(url))
        img?.scaleToFit(100f, 100f)
        img?.let { imgCell.addElement(it) }
    }
    tableEvent.addCell(imgCell)

    // Datos del evento
    val info = Paragraph()
    info.add(Chunk("${ticket.nombreEvento}\n", fontEvento))
    info.add(Chunk("Inicio del evento: $eventoFecha\n", fontTexto))
    info.add(Chunk("Fecha de compra: ${ticket.fechaCompra}\n", fontTexto))
    info.add(Chunk("Precio: ${String.format("%.2f €", ticket.precioPagado)}", fontTexto))

    val infoCell = PdfPCell(info)
    infoCell.border = Rectangle.NO_BORDER
    infoCell.backgroundColor = azul
    tableEvent.addCell(infoCell)

    // Código QR
    val qrCell = PdfPCell()
    qrCell.border = Rectangle.NO_BORDER
    qrCell.backgroundColor = azul
    val qrImg = cargarImagen(construirUrlImagen(ticket.codigoQR))
    qrImg?.scaleToFit(90f, 90f)
    qrImg?.let { qrCell.addElement(it) }
    tableEvent.addCell(qrCell)

    tableMain.addCell(tableEvent)

    // ---Sección inferior---
    val infoInferior = """
        ID Ticket: ${ticket.id}
        Usuario ID: ${ticket.usuarioId}
        Evento ID: ${ticket.eventoId}
        Nombre del evento: ${ticket.nombreEvento}
        Inicio del evento: $eventoFecha
    """.trimIndent()

    val bottomCell = PdfPCell(Phrase(infoInferior, fontTextoNegro))
    bottomCell.backgroundColor = grisFondo
    bottomCell.border = Rectangle.NO_BORDER
    bottomCell.paddingTop = 10f
    bottomCell.paddingBottom = 10f
    tableMain.addCell(bottomCell)

    val note = PdfPCell(Phrase("Prohibida la reventa. No reembolsable. Mostrar en la entrada del evento.", fontPeque))
    note.border = Rectangle.NO_BORDER
    note.horizontalAlignment = Element.ALIGN_CENTER
    tableMain.addCell(note)

    document.add(tableMain)
    document.close()

    val pdfBytes = file.readBytes()
    return Base64.encodeToString(pdfBytes, Base64.NO_WRAP)
}

/**
 * Carga una imagen desde URL o Base64 (devuelve Image de iText)
 */
private fun cargarImagen(ruta: String?): Image? {
    if (ruta.isNullOrBlank()) return null
    return try {
        val bytes = when {
            ruta.startsWith("http") -> URL(ruta).openStream().use { it.readBytes() }
            ruta.startsWith("data:image") -> {
                val base64 = ruta.substringAfter(",")
                Base64.decode(base64, Base64.DEFAULT)
            }
            else -> Base64.decode(ruta, Base64.DEFAULT)
        }
        Image.getInstance(bytes)
    } catch (e: Exception) {
        Log.e("PDF", "Error cargando imagen: ${e.message}")
        null
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