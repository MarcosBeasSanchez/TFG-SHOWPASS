package com.example.appmovilshowpass.viewmodel

import android.content.Context
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.ContentView
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

class TicketViewModel: ViewModel() {

    // Flujo de tickets cargados del backend
    private val _tickets = MutableStateFlow<List<DTOTicketBajada>>(emptyList())
    val tickets: StateFlow<List<DTOTicketBajada>> get() = _tickets

    /**
     * Llama al backend para obtener todos los tickets de un usuario.
     */

    fun cargarTickets(usuarioId: Long) {
        viewModelScope.launch {
            try {
                _tickets.value = RetrofitClient.ticketApiService.obtenerTicketsPorUsuario(usuarioId)
            }
            catch (e: Exception){
                e.printStackTrace()
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
                // 🔹 1. Obtener el evento completo desde el backend
                val eventoDto = RetrofitClient.eventoApiService.findById(ticket.eventoId)
                val evento = eventoDto.toEvento()

                // 🔹 2. Generar el PDF con los datos reales del evento
                val pdfBase64 = generarTicketPdf(
                    context,
                    evento.nombre, // nombre del evento
                    "TICKET-${ticket.id}",
                    evento.inicioEvento.take(16), // fecha del evento
                    construirUrlImagen(evento.imagen) // imagen completa del evento
                )

                // 🔹 3. Guardar en carpeta Descargas
                val pdfBytes = Base64.decode(pdfBase64, Base64.NO_WRAP)
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "${evento.nombre}-${ticket.id}.pdf")

                FileOutputStream(file).use { it.write(pdfBytes) }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "📄 PDF guardado en Descargas", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Error al generar PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun enviarTicketPorEmail(context: Context, email: String, ticket: DTOTicketBajada) {
        viewModelScope.launch {
            try {
                // 🔹 1. Obtener el evento por ID
                val eventoDto = RetrofitClient.eventoApiService.findById(ticket.eventoId)
                val evento = eventoDto.toEvento()

                // 🔹 2. Generar el PDF (igual que en generarPdfTicket)
                val pdfBase64 = generarTicketPdf(
                    context,
                    evento.nombre,
                    "TICKET-${ticket.id}",
                    evento.inicioEvento.take(16),
                    construirUrlImagen(evento.imagen)
                )

                // 🔹 3. Enviar PDF por email usando la API
                val payload = mapOf(
                    "email" to email,
                    "ticketId" to "TICKET-${ticket.id}",
                    "eventoNombre" to evento.nombre,
                    "pdfBase64" to pdfBase64
                )

                val response = RetrofitClient.ticketApiService.enviarPdfEmail(payload)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val mensaje = response.body()?.get("mensaje") ?: "Ticket enviado correctamente"
                        Toast.makeText(context, "📧 $mensaje", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "⚠️ Error del servidor (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Error al enviar el ticket", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun vaciarTickets( context: Context, usuarioId: Long) {
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
                        Log.d("TicketVM", " Tickets antes: ${_tickets.value.size}, después: ${nuevaLista.size}")
                        _tickets.value = nuevaLista
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("TicketVM", " Error HTTP al eliminar: ${response.code()} ${response.message()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No se pudo eliminar el ticket", Toast.LENGTH_SHORT).show()
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

}