package com.example.appmovilshowpass.viewmodel

import android.content.Context
import android.os.Environment
import android.util.Base64
import android.widget.Toast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOTicketBajada
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
                val pdfBase64 = generarTicketPdf(
                    context,
                    ticket.eventoNombre,
                    "TICKET-${ticket.id}",
                    ticket.eventoInicio.take(16),
                    ticket.eventoImagen
                )

                val pdfBytes = Base64.decode(pdfBase64, Base64.NO_WRAP)
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "${ticket.eventoNombre}-${ticket.id}.pdf")
                FileOutputStream(file).use { it.write(pdfBytes) }

                Toast.makeText(context, "📄 PDF guardado en Descargas", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "❌ Error al generar PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun enviarTicketPorEmail(context: Context, email: String, ticket: DTOTicketBajada) {
        viewModelScope.launch {
            try {
                val pdfBase64 = generarTicketPdf(
                    context,
                    ticket.eventoNombre,
                    "TICKET-${ticket.id}",
                    ticket.eventoInicio.take(16),
                    ticket.eventoImagen
                )

                val payload = mapOf(
                    "email" to email,
                    "ticketId" to "TICKET-${ticket.id}",
                    "eventoNombre" to ticket.eventoNombre,
                    "pdfBase64" to pdfBase64
                )

                val response = RetrofitClient.ticketApiService.enviarPdfEmail(payload)

                if (response.isSuccessful) {
                    val mensaje = response.body()?.get("mensaje") ?: "Ticket enviado correctamente"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "📧 $mensaje", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
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
                    Toast.makeText(context, "❌ Error al enviar el ticket", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun vaciarTickets() {
        viewModelScope.launch {
            _tickets.value = emptyList()
        }
    }

}