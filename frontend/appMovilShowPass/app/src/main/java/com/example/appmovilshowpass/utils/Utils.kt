package com.example.appmovilshowpass.utils


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
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.lang.Exception

// Formatea una fecha y hora en formato ISO (ejemplo: 2025-02-01T12:30:00)
// De forma predeterminada devuelve el formato dd/MM/yyyy HH:mm
// Si la cadena no se puede parsear, devuelve la fecha original sin cambios.
fun formatearFechayHora(fecha: String, formato: String = "dd/MM/yyyy HH:mm"): String {
    return try {
        val parsed = LocalDateTime.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        parsed.format(DateTimeFormatter.ofPattern(formato))
    } catch (e: Exception) {
        fecha
    }
}

// Formatea una fecha en formato ISO sin hora (ejemplo 2025-02-01).
// Devuelve el formato dd/MM/yyyy por defecto.
// Si no se puede parsear, devuelve la cadena original.
fun formatearFecha(fecha: String, formato: String = "dd/MM/yyyy"): String {
    return try {
        val parsed = LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE)
        parsed.format(DateTimeFormatter.ofPattern(formato))
    } catch (e: Exception) {
        fecha
    }
}

// Formatea un precio con dos decimales fijos.
fun formatearPrecio(precio: Double): String {
    return String.format("%.2f", precio)
}

/**
 * Genera un ticket en formato PDF y lo devuelve codificado en Base64.
 * El archivo se crea temporalmente en el directorio de caché de la aplicación.
 * Este método construye varios bloques visuales del ticket: encabezado, bloque azul con datos
 * principales, QR y bloque gris inferior con información detallada.
 */
fun generarTicketPdf(
    context: Context,
    ticket: DTOTicketBajada,
    eventoFecha: String,
    eventoImagenUrl: String?
): String {

    // Archivo PDF temporal.
    val file = File(context.cacheDir, "ticket_${ticket.id}.pdf")

    // Configuración del documento PDF.
    val document = Document(PageSize.A5, 30f, 30f, 30f, 30f)
    PdfWriter.getInstance(document, FileOutputStream(file))
    document.open()

    // Paleta de colores utilizada en el diseño.
    val azulOscuro = BaseColor(0, 45, 98)
    val grisFondo = BaseColor(240, 240, 240)
    val amarilloTitulo = BaseColor(255, 204, 0)
    val grisTexto = BaseColor(120, 120, 120)

    // Definición de las fuentes utilizadas en el documento.
    val fontHeaderAzul = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD, azulOscuro)
    val fontEvento = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, amarilloTitulo )
    val fontTextoBlanco = Font(Font.FontFamily.HELVETICA, 7f, Font.BOLD, BaseColor.WHITE)
    val fontNegro = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, BaseColor.BLACK)
    val fontNegroBold = Font(Font.FontFamily.HELVETICA, 8f, Font.BOLD, BaseColor.BLACK)
    val fontGris = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, grisTexto)

    // Encabezado con el nombre de la aplicación.
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

    // Bloque azul principal con imagen, información y QR.
    val tablaAzul = PdfPTable(floatArrayOf(1f, 2.3f, 1f)).apply {
        widthPercentage = 100f
        spacingBefore = 4f
        spacingAfter = 6f

        // Se limpian posibles configuraciones internas para asegurar bordes consistentes.
        this.tableEvent = null
        this.defaultCell.cellEvent = null
        this.defaultCell.borderWidth = 0f
    }

    val alturaFila = 110f

    // Celda izquierda: imagen del evento.
    val imgCell = PdfPCell().apply {
        backgroundColor = azulOscuro
        border = Rectangle.NO_BORDER
        setPadding(2f)
        setFixedHeight(alturaFila)
        horizontalAlignment = Element.ALIGN_CENTER
        verticalAlignment = Element.ALIGN_MIDDLE
    }

    // Intento de carga de la imagen del evento.
    eventoImagenUrl?.let { url ->
        val fullUrl = construirUrlImagen(url)
        val img = cargarImagen(fullUrl)
        if (img != null) {
            img.scaleToFit(75f, 65f)
            img.alignment = Element.ALIGN_CENTER
            imgCell.addElement(img)
        }
    }
    tablaAzul.addCell(imgCell)

    // Celda central: texto descriptivo del evento.
    val infoEvento = Paragraph().apply {
        add(Chunk("${ticket.nombreEvento}\n", fontEvento))
        add(Chunk("Inicio del evento: ${formatearFechaFlexible(eventoFecha)}\n", fontTextoBlanco))
        add(Chunk("Fecha de compra: ${formatearFechaFlexible(ticket.fechaCompra)}\n", fontTextoBlanco))
        add(Chunk("Precio: ${"%.2f €".format(ticket.precioPagado)}", fontTextoBlanco))
        setLeading(18f, 0f)
    }

    val infoCell = PdfPCell(infoEvento).apply {
        backgroundColor = azulOscuro
        border = Rectangle.NO_BORDER
        setPadding(2f)
        setFixedHeight(alturaFila)
        verticalAlignment = Element.ALIGN_MIDDLE
    }
    tablaAzul.addCell(infoCell)

    // Celda derecha: código QR del ticket.
    val qrCell = PdfPCell().apply {
        backgroundColor = azulOscuro
        border = Rectangle.NO_BORDER
        setPadding(2f)
        setFixedHeight(alturaFila)
        horizontalAlignment = Element.ALIGN_CENTER
        verticalAlignment = Element.ALIGN_MIDDLE
    }

    val qrUrl = construirUrlImagen(ticket.codigoQR)
    val qrImg = cargarImagen(qrUrl)
    if (qrImg != null) {
        qrImg.scaleToFit(75f, 85f)
        qrImg.alignment = Element.ALIGN_CENTER
        qrCell.addElement(qrImg)
    }
    tablaAzul.addCell(qrCell)

    document.add(tablaAzul)

    // Bloque gris inferior con información adicional del ticket.
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

    // El documento ya está completo y se cierra.
    document.close()

    // Codificación del PDF en Base64 para su envío o almacenamiento.
    val pdfBytes = file.readBytes()
    return Base64.encodeToString(pdfBytes, Base64.NO_WRAP)
}

/**
 * Carga una imagen desde una URL o desde una cadena Base64.
 * La función detecta automáticamente el formato según el prefijo recibido.
 * Si ocurre algún error, devuelve null.
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
                        ByteArray(0)
                    }
                }
            }
            ruta.startsWith("data:image") ->
                Base64.decode(ruta.substringAfter(","), Base64.DEFAULT)

            else ->
                Base64.decode(ruta, Base64.DEFAULT)
        }
        if (bytes.isNotEmpty()) Image.getInstance(bytes) else null
    } catch (e: Exception) {
        null
    }
}

/**
 * Intenta formatear diversas variantes de fecha y hora, normalmente recibidas del servidor.
 * Soporta varios patrones comunes y devuelve un formato unificado: d/M/yyyy, HH:mm:ss.
 * Si no se reconoce el formato, devuelve la cadena original.
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
            val parsed = LocalDateTime.parse(
                fecha.take(19).replace('T',' '),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]".replace("[:ss]", if (fecha.length>=19) ":ss" else ""))
            )
            return parsed.format(salida)
        } catch (_: Exception) {}

        try {
            return LocalDateTime.parse(
                fecha.substring(0, minOf(fecha.length, pat.length)),
                DateTimeFormatter.ofPattern(pat)
            ).format(salida)
        } catch (_: Exception) {}
    }

    // Último intento para formatos tipo yyyy-MM-dd'T'HH:mm.
    return try {
        val entrada = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        LocalDateTime.parse(fecha.substring(0, 16), entrada).format(salida)
    } catch (_: Exception) {
        fecha
    }
}

/**
 * Construye una URL completa para una imagen.
 * Según el prefijo, determina si ya es una URL válida, parte del servidor, o Base64.
 * Esto garantiza que las imágenes puedan mostrarse correctamente desde cualquier fuente.
 */
fun construirUrlImagen(ruta: String?): String {
    if (ruta.isNullOrBlank()) return ""

    return when {
        ruta.startsWith("http://") || ruta.startsWith("https://") ->
            ruta

        ruta.startsWith("/uploads/") ->
            SERVER_BASE_URL_FOTOS + ruta

        ruta.startsWith("uploads/") ->
            "$SERVER_BASE_URL_FOTOS/$ruta"

        ruta.startsWith("data:image/") ->
            ruta

        else ->
            "data:image/png;base64,$ruta"
    }
}

/**
 * Convierte una imagen obtenida mediante un Uri en una cadena Base64.
 * Es útil para almacenar imágenes o enviarlas a un servidor sin necesidad de archivos temporales.
 */
fun imagenToBase64(context: Context, uri: Uri): String {
    val input = context.contentResolver.openInputStream(uri)
    val bytes = input?.readBytes() ?: return ""
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}