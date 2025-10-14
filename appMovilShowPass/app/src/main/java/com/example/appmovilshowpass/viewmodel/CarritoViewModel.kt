package com.example.appmovilshowpass.viewmodel
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOTicketSubida
import com.example.appmovilshowpass.data.remote.dto.toCarrito
import com.example.appmovilshowpass.model.Carrito
import com.example.appmovilshowpass.model.Evento
import com.example.appmovilshowpass.utils.generarTicketPdf
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
                val eventos = _carrito.value?.eventos ?: emptyList()
                _eventosComprados.value = eventos

<<<<<<< HEAD
                // Llamamos al endpoint para finalizar el carrito
                RetrofitClient.carritoApiService.finalizarCompra(usuarioId)

                //  Por cada evento, generamos su ticket
                for (evento in eventos) {
                    try {
                        val dtoTicket = DTOTicketSubida(
                            usuarioId = usuarioId,
                            eventoId = evento.id,
                            precio = evento.precio
                        )
                        RetrofitClient.ticketApiService.insertarTicket(dtoTicket)
                        Log.d("CarritoVM", "🎟 Ticket generado para ${evento.nombre}")
                    } catch (t: Throwable) {
                        Log.e("CarritoVM", "❌ Error generando ticket: ${t.message}")
                    }
                }

                // 3️⃣ Limpiamos carrito
=======
                // Solo llamamos al backend
                RetrofitClient.carritoApiService.finalizarCompra(usuarioId)

                // Limpiar carrito
>>>>>>> 82ef1b0 (Resolver conflicto: mantener TicketViewModel.kt desde mis cambios)
                _carrito.value = null
                _total.value = 0.0

                onSuccess()
<<<<<<< HEAD

=======
>>>>>>> 82ef1b0 (Resolver conflicto: mantener TicketViewModel.kt desde mis cambios)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



}