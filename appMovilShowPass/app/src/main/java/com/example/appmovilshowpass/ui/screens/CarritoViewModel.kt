package com.example.appmovilshowpass.ui.screens
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.local.generarTicketPdf
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.toCarrito
import com.example.appmovilshowpass.model.Carrito
import com.example.appmovilshowpass.model.Evento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    private val _carrito = MutableStateFlow<Carrito?>(null)
    val carrito: StateFlow<Carrito?> get() = _carrito

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> get() = _total

    // ✅ Nuevo estado para guardar los eventos comprados
    private val _eventosComprados = MutableStateFlow<List<Evento>>(emptyList())
    val eventosComprados: StateFlow<List<Evento>> get() = _eventosComprados

    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            try {
                val dto = RetrofitClient.carritoApiService.obtenerCarrito(usuarioId)
                _carrito.value = dto.toCarrito()
                _total.value = RetrofitClient.carritoApiService.calcularTotal(usuarioId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun agregarEvento(usuarioId: Long, eventoId: Long) {
        viewModelScope.launch {
            try {
                Log.d("CarritoVM", "Agregando evento: user=$usuarioId evento=$eventoId") // 👈 DEBUG
                val dto = RetrofitClient.carritoApiService.agregarEvento(usuarioId, eventoId)
                _carrito.value = dto.toCarrito()
                _total.value = RetrofitClient.carritoApiService.calcularTotal(usuarioId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun eliminarEvento(usuarioId: Long, eventoId: Long) {
        viewModelScope.launch {
            val dto = RetrofitClient.carritoApiService.eliminarEvento(usuarioId, eventoId)
            _carrito.value = dto.toCarrito()
            _total.value = RetrofitClient.carritoApiService.calcularTotal(usuarioId)
        }
    }

    fun vaciarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            val dto = RetrofitClient.carritoApiService.vaciarCarrito(usuarioId)
            _carrito.value = dto.toCarrito()
            _total.value = 0.0
        }
    }

    fun finalizarCompra(usuarioId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // ✅ Guardamos los eventos ANTES de vaciar el carrito
                _eventosComprados.value = _carrito.value?.eventos ?: emptyList()

                RetrofitClient.carritoApiService.finalizarCompra(usuarioId)

                // Limpieza del carrito
                _carrito.value = null
                _total.value = 0.0

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun generarYGuardarPdf(context: Context, eventos: List<Evento>) {
        viewModelScope.launch {
            try {
                eventos.forEachIndexed { _, evento ->
                    val pdfBase64 = generarTicketPdf(context, evento.nombre, "TICKET-${evento.id}")
                    Log.d("CarritoViewModel", "PDF generado para ${evento.nombre}: $pdfBase64")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun enviarTicketsPorEmail(context: Context, eventos: List<Evento>) {
        viewModelScope.launch {
            try {
                eventos.forEach { evento ->
                    val pdfBase64 = generarTicketPdf(context, evento.nombre, "TICKET-${evento.id}")
                    val payload = mapOf(
                        "email" to "usuario@correo.com", // ← cámbialo al email real del AuthViewModel
                        "ticketId" to "TICKET-${evento.id}",
                        "eventoNombre" to evento.nombre,
                        "pdfBase64" to pdfBase64
                    )
                    RetrofitClient.carritoApiService.enviarPdfEmail(payload)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}