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

class TicketViewModel : ViewModel() {

    // Flujo de tickets cargados del backend
    private val _tickets = MutableStateFlow<List<DTOTicketBajada>>(emptyList())
    val tickets: StateFlow<List<DTOTicketBajada>> get() = _tickets

    //Validación QR de tickets
    private val _resultadoQR = MutableStateFlow<ResultadoQR?>(null) //Estado inicial nulo
    val resultadoQR: StateFlow<ResultadoQR?> = _resultadoQR //Enum con estados

    /**
     * Llama al backend para obtener todos los tickets de un usuario.
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
     * Genera y guarda localmente el PDF del ticket seleccionado.
     * El archivo se guarda en la carpeta "Download" del dispositivo.
     */
    fun generarPdfTicket(context: Context, ticket: DTOTicketBajada) {
        viewModelScope.launch {
            try {
                //  Obtener el evento completo desde el backend
                val eventoDto = RetrofitClient.eventoApiService.findById(ticket.eventoId)
                val evento = eventoDto.toEvento()

                //  Generar el PDF (usa ticket completo)
                val pdfBase64 = generarTicketPdf(
                    context = context,
                    ticket = ticket,
                    eventoFecha = evento.inicioEvento.take(16),
                    eventoImagenUrl = construirUrlImagen(evento.imagen)
                )

                // 3.Guardar en carpeta Descargas
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
     *  Genera el ticket y lo envía por correo electrónico usando el backend.
     */
    fun enviarTicketPorEmail(context: Context, email: String, ticket: DTOTicketBajada) {
        viewModelScope.launch {
            try {
                // Obtener el evento por ID
                val eventoDto = RetrofitClient.eventoApiService.findById(ticket.eventoId)
                val evento = eventoDto.toEvento()

                // Generar PDF (misma lógica)
                val pdfBase64 = generarTicketPdf(
                    context = context,
                    ticket = ticket,
                    eventoFecha = evento.inicioEvento.take(16),
                    eventoImagenUrl = construirUrlImagen(evento.imagen)
                )

                // Enviar PDF por email
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
                        Toast.makeText(context, "$mensaje", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(
                        context,
                        "No se pudieron eliminar los tickets",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error al eliminar tickets", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun eliminarTicket(context: Context, id: Long) {
        viewModelScope.launch {
            try {
                Log.d("TicketVM", " Intentando eliminar ticket con id=$id")

                val response = RetrofitClient.ticketApiService.eliminarTicket(id)
                Log.d("TicketVM", " Respuesta HTTP: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("TicketVM", "Cuerpo de respuesta: $body")

                    val status = body?.get("status")?.toString()
                    val mensaje = body?.get("mensaje")?.toString() ?: "Operación completada"

                    Log.d("TicketVM", " Status: $status | Mensaje: $mensaje")

                    if (status == "success") {
                        Log.d("TicketVM", "Eliminando ticket de la lista local...")
                        val nuevaLista = _tickets.value.toMutableList().filterNot { it.id == id }
                        Log.d(
                            "TicketVM",
                            " Tickets antes: ${_tickets.value.size}, después: ${nuevaLista.size}"
                        )
                        _tickets.value = nuevaLista
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(
                        "TicketVM",
                        " Error HTTP al eliminar: ${response.code()} ${response.message()}"
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No se pudo eliminar el ticket", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Log.e("TicketVM", " Excepción eliminando ticket: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al eliminar ticket", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun resetearResultadoQR() {
        _resultadoQR.value = null
    }

    /**
     * Recibe un código QR, lo envía al backend para validación y actualiza el estado del resultado.
     * @param codigoQR El código QR a validar.
     *
     */

    fun validarQr(codigoQR: String) {
        viewModelScope.launch {
            try {
                _resultadoQR.value = ResultadoQR.CARGANDO
                val response = RetrofitClient.ticketApiService.validarCodigoQR(codigoQR)
                Log.d("TicketVM", "Respuesta HTTP: $response")

                // Suponiendo que la API devuelve un booleano (true = válido, false = no válido)
                if (response.isSuccessful) { //200 ok
                    val esValido: Boolean? = response.body() // <-- obtenemos el Boolean
                    Log.d("TicketVM", "Cuerpo de respuesta: $esValido")
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
/*
*  Enum para representar los posibles resultados de la validación de un código QR.
* */
enum class ResultadoQR {
    CARGANDO, VALIDO, INVALIDO, ERROR
}



