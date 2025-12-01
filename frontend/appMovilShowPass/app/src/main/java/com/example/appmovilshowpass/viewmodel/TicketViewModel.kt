package com.example.appmovilshowpass.viewmodel

import android.content.Context
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.utils.construirUrlImagen
import com.example.appmovilshowpass.utils.generarTicketPdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * ViewModel encargado de gestionar todas las operaciones relacionadas con tickets:
 * - Cargar los tickets del usuario
 * - Generar y guardar PDFs
 * - Enviar tickets por correo electrónico
 * - Eliminar tickets individualmente o en bloque
 * - Validación de códigos QR
 *
 * Utiliza StateFlow para mantener la interfaz sincronizada con el estado de los datos.
 */
class TicketViewModel : ViewModel() {

    // Flujo que contiene la lista de tickets obtenidos del backend.
    private val _tickets = MutableStateFlow<List<DTOTicketBajada>>(emptyList())
    val tickets: StateFlow<List<DTOTicketBajada>> get() = _tickets

    // Estado de la validación de un código QR.
    private val _resultadoQR = MutableStateFlow<ResultadoQR?>(null)
    val resultadoQR: StateFlow<ResultadoQR?> = _resultadoQR

    /**
     * Obtiene todos los tickets de un usuario desde el backend.
     * La lista recibida se publica en el StateFlow correspondiente.
     */
    fun cargarTickets(usuarioId: Long) {
        viewModelScope.launch {
            try {
                Log.d("TicketVM", "Cargando tickets del usuario $usuarioId...")

                val result = RetrofitClient.ticketApiService.obtenerTicketsPorUsuario(usuarioId)

                Log.d("TicketVM", "Tickets recibidos: ${result.size}")
                result.forEach {
                    Log.d(
                        "TicketVM",
                        "Ticket -> id=${it.id}, evento=${it.nombreEvento}, precio=${it.precioPagado}"
                    )
                }

                _tickets.value = result

            } catch (e: Exception) {
                Log.e("TicketVM", "Error al cargar tickets: ${e.message}")
            }
        }
    }

    /**
     * Genera el PDF asociado a un ticket y lo guarda en la carpeta "Download" del dispositivo.
     * Utiliza generarTicketPdf(), que se encarga de construir el archivo en Base64.
     */
    fun generarPdfTicket(context: Context, ticket: DTOTicketBajada) {
        viewModelScope.launch {
            try {
                // Obtener información completa del evento (necesaria para el PDF)
                val eventoDto = RetrofitClient.eventoApiService.findById(ticket.eventoId)
                val evento = eventoDto.toEvento()

                // Generar PDF como Base64
                val pdfBase64 = generarTicketPdf(
                    context = context,
                    ticket = ticket,
                    eventoFecha = evento.inicioEvento.take(16),
                    eventoImagenUrl = construirUrlImagen(evento.imagen)
                )

                // Guardar el PDF en la carpeta Descargas
                val pdfBytes = Base64.decode(pdfBase64, Base64.NO_WRAP)
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                val safeName = evento.nombre.replace("[^a-zA-Z0-9-_]".toRegex(), "_")
                val file = File(downloadsDir, "${safeName}-${ticket.id}.pdf")

                FileOutputStream(file).use { it.write(pdfBytes) }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "PDF guardado en Descargas", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al generar PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Genera el PDF asociado a un ticket y lo envía por correo electrónico
     * utilizando el servicio backend.
     */
    fun enviarTicketPorEmail(context: Context, email: String, ticket: DTOTicketBajada) {
        viewModelScope.launch {
            try {
                val eventoDto = RetrofitClient.eventoApiService.findById(ticket.eventoId)
                val evento = eventoDto.toEvento()

                val pdfBase64 = generarTicketPdf(
                    context = context,
                    ticket = ticket,
                    eventoFecha = evento.inicioEvento.take(16),
                    eventoImagenUrl = construirUrlImagen(evento.imagen)
                )

                // Payload enviado al backend con el PDF codificado
                val payload = mapOf(
                    "email" to email,
                    "ticketId" to ticket.id.toString(),
                    "eventoNombre" to ticket.nombreEvento,
                    "pdfBase64" to pdfBase64
                )

                val response = RetrofitClient.ticketApiService.enviarPdfEmail(payload)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val mensaje =
                            response.body()?.get("mensaje") ?: "Ticket enviado correctamente"
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Error del servidor (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al enviar el ticket", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Elimina todos los tickets del usuario en el backend.
     * También actualiza la lista local para que la UI refleje el cambio.
     */
    fun vaciarTickets(context: Context, usuarioId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.ticketApiService.eliminarTodosLosTickets(usuarioId)

                if (response.isSuccessful) {
                    val body = response.body()
                    val mensaje = body?.get("mensaje") ?: "Operación completada"
                    val status = body?.get("status")

                    if (status == "success" || status == "empty") {
                        _tickets.value = emptyList()
                    }

                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context, "No se pudieron eliminar los tickets", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error al eliminar tickets", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Elimina un ticket concreto por su ID.
     * Si la operación tiene éxito, también se elimina del StateFlow local.
     */
    fun eliminarTicket(context: Context, id: Long) {
        viewModelScope.launch {
            try {
                Log.d("TicketVM", "Intentando eliminar ticket con id=$id")

                val response = RetrofitClient.ticketApiService.eliminarTicket(id)

                Log.d("TicketVM", "Respuesta HTTP: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    val status = body?.get("status")?.toString()
                    val mensaje = body?.get("mensaje")?.toString() ?: "Operación completada"

                    if (status == "success") {
                        // Eliminación local del ticket
                        val nuevaLista = _tickets.value.filterNot { it.id == id }
                        _tickets.value = nuevaLista
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No se pudo eliminar el ticket", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            } catch (e: Exception) {
                Log.e("TicketVM", "Excepción eliminando ticket: ${e.message}", e)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al eliminar ticket", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Restablece el estado de la validación QR a nulo.
     * Se usa para reiniciar la UI después de mostrar el resultado.
     */
    fun resetearResultadoQR() {
        _resultadoQR.value = null
    }

    /**
     * Envía un código QR al backend para validar su autenticidad.
     * Actualiza el StateFlow resultadoQR con el estado correspondiente.
     */
    fun validarQr(codigoQR: String) {
        viewModelScope.launch {
            try {
                _resultadoQR.value = ResultadoQR.CARGANDO

                val response = RetrofitClient.ticketApiService.validarCodigoQR(codigoQR)
                Log.d("TicketVM", "Respuesta HTTP: $response")

                if (response.isSuccessful) {
                    val esValido: Boolean? = response.body()

                    if (esValido == true) {
                        _resultadoQR.value = ResultadoQR.VALIDO
                    } else {
                        _resultadoQR.value = ResultadoQR.INVALIDO
                    }
                } else {
                    _resultadoQR.value = ResultadoQR.ERROR
                }

            } catch (e: Exception) {
                Log.e("TicketVM", "Excepción validando QR: ${e.message}")
                _resultadoQR.value = ResultadoQR.ERROR
            }
        }
    }
}

/**
 * Enum que representa los posibles estados del resultado en la validación de un código QR.
 */
enum class ResultadoQR {
    CARGANDO,
    VALIDO,
    INVALIDO,
    ERROR
}