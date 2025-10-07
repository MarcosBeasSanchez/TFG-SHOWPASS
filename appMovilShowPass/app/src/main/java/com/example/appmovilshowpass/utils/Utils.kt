package com.example.appmovilshowpass.utils

import android.content.Context
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Base64

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.*
import java.net.URL
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.itextpdf.text.pdf.PdfContentByte
import kotlinx.coroutines.Dispatchers

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
/**
 * Genera un ticket en formato PDF con:
 *  - Logo y título
 *  - Imagen del evento
 *  - Información del evento (nombre, fecha, id)
 *  - Código QR centrado
 *  - Pie de página
 *
 * Devuelve el PDF en Base64 (útil para enviarlo por email).
 */
fun generarTicketPdf(
    context: Context,
    eventoNombre: String,
    ticketId: String,
    eventoFecha: String = "",
    eventoImagenUrl: String? = null
): String {
    val file = File(context.cacheDir, "$ticketId.pdf")
    val document = Document(PageSize.A6)
    val writer = PdfWriter.getInstance(document, FileOutputStream(file))
    document.open()

    val canvas: PdfContentByte = writer.directContent
    val azul = BaseColor(0, 102, 204)

    val fontTitulo = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD, azul)
    val fontTexto = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)
    val fontPeque = Font(Font.FontFamily.HELVETICA, 8f, Font.ITALIC, BaseColor.DARK_GRAY)

    // --- 🟦 Dibuja borde redondeado doble ---
    val rect = document.pageSize
    val margen = 8f
    val cb = canvas
    cb.setColorStroke(azul)

    // Borde externo grueso
    cb.setLineWidth(5f)
    cb.roundRectangle(margen, margen, rect.width - 2 * margen, rect.height - 2 * margen, 16f)
    cb.stroke()

    // Borde interno fino
    cb.setLineWidth(1.5f)
    cb.roundRectangle(margen + 5, margen + 5, rect.width - 2 * (margen + 5), rect.height - 2 * (margen + 5), 12f)
    cb.stroke()

    // --- 🎟️ Título ---
    val titulo = Paragraph("🎟 SHOWPASS TICKET", fontTitulo)
    titulo.alignment = Element.ALIGN_CENTER
    document.add(titulo)
    document.add(Paragraph("\n"))

    // --- 🔵 Línea separadora ---
    val line = Paragraph("─".repeat(40), fontTexto)
    line.alignment = Element.ALIGN_CENTER
    document.add(line)
    document.add(Paragraph("\n"))

    // --- 🖼 Imagen del evento ---
    eventoImagenUrl?.let { raw ->
        try {
            Log.d("TicketPDF", "Imagen del ticket: ${raw.take(60)}")

            val imageBytes: ByteArray = if (raw.startsWith("http")) {
                kotlinx.coroutines.runBlocking(Dispatchers.IO) {
                    try {
                        URL(raw).openStream().use { it.readBytes() }
                    } catch (e: Exception) {
                        Log.e("TicketPDF", "❌ Error descargando imagen: ${e.message}")
                        ByteArray(0)
                    }
                }
            } else {
                Base64.decode(raw, Base64.DEFAULT)
            }

            if (imageBytes.isNotEmpty()) {
                val bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                val img = Image.getInstance(stream.toByteArray())
                img.scaleToFit(180f, 100f) // 📏 Imagen más pequeña
                img.alignment = Element.ALIGN_CENTER
                document.add(img)
                document.add(Paragraph("\n"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TicketPDF", "❌ Error cargando imagen: ${e.message}")
        }
    }

    // --- 📄 Información del ticket ---
    val info = Paragraph("", fontTexto)
    info.alignment = Element.ALIGN_CENTER
    info.add("Evento: $eventoNombre\n")
    if (eventoFecha.isNotBlank()) info.add("Fecha evento: $eventoFecha\n")
    val fechaCompra = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    info.add("Fecha compra: $fechaCompra\n")
    info.add("Ticket ID: $ticketId\n")
    document.add(info)
    document.add(Paragraph("\n"))

    // --- 🔲 Código QR ---
    val qrBitmap = generarQR(ticketId)
    val qrStream = ByteArrayOutputStream()
    qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, qrStream)
    val qrImage = Image.getInstance(qrStream.toByteArray())
    qrImage.scaleToFit(90f, 90f)
    qrImage.alignment = Element.ALIGN_CENTER
    document.add(qrImage)
    document.add(Paragraph("\n"))

    // --- 🔵 Línea separadora inferior ---
    val lineBottom = Paragraph("─".repeat(40), fontTexto)
    lineBottom.alignment = Element.ALIGN_CENTER
    document.add(lineBottom)
    document.add(Paragraph("\n"))

    // --- 🎵 Pie ---
    val pie = Paragraph("Muestra este ticket en la entrada 🎶", fontPeque)
    pie.alignment = Element.ALIGN_CENTER
    document.add(pie)

    document.close()

    val pdfBytes = file.readBytes()
    return Base64.encodeToString(pdfBytes, Base64.NO_WRAP)
}


    /**
 * Genera una imagen QR Bitmap a partir de un texto.
 * Usado para incluir el código QR dentro del PDF.
 */
private fun generarQR(texto: String): Bitmap {
    val size = 300
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, size, size)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bmp[x, y] =
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }
    return bmp
}