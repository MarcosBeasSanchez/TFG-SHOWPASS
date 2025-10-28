package com.example.appmovilshowpass.viewmodel
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOCarritoItemSubida
import com.example.appmovilshowpass.data.remote.dto.DTOTicketSubida
import com.example.appmovilshowpass.data.remote.dto.toCarrito
import com.example.appmovilshowpass.model.Carrito
import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.model.EstadoCarrito
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

    // Eventos comprados (solo si los necesitas)
    private val _eventosComprados = MutableStateFlow<List<Evento>>(emptyList())
    val eventosComprados: StateFlow<List<Evento>> get() = _eventosComprados

    /**
     * Cargar carrito del usuario desde el backend
     */
    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            try {
                val dto = RetrofitClient.carritoApiService.obtenerCarrito(usuarioId)
                val carrito = dto.toCarrito()
                _carrito.value = carrito
                calcularTotal(carrito)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CarritoVM", "Error al cargar carrito: ${e.message}")
            }
        }
    }

    /**
     * Agregar un evento al carrito (crea un CarritoItem nuevo)
     */
    fun agregarItem(usuarioId: Long, eventoId: Long, cantidad: Int = 1) {
        viewModelScope.launch {
            try {
                Log.d("CarritoVM", "Agregando item: user=$usuarioId evento=$eventoId cantidad=$cantidad")

                val body = mapOf("cantidad" to cantidad)
                val dto = RetrofitClient.carritoApiService.agregarItem(usuarioId, eventoId, body)

                val carrito = dto.toCarrito()
                _carrito.value = carrito
                calcularTotal(carrito)

                Log.d("CarritoVM", "Item agregado correctamente. Nuevo total: ${_total.value}")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CarritoVM", "Error al agregar item: ${e.message}")
            }
        }
    }

    /**
     * Eliminar un item específico del carrito
     */
    fun eliminarItem(usuarioId: Long, itemId: Long) {
        viewModelScope.launch {
            try {
                Log.d("CarritoVM", "Eliminando item: $itemId del usuario $usuarioId")
                val dto = RetrofitClient.carritoApiService.eliminarItem(usuarioId, itemId)
                val carrito = dto.toCarrito()
                _carrito.value = carrito
                calcularTotal(carrito)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CarritoVM", "Error al eliminar item: ${e.message}")
            }
        }
    }

    /**
     * Vaciar completamente el carrito
     */
    fun vaciarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            try {
                Log.d("CarritoVM", "Vaciando carrito de usuario $usuarioId")
                RetrofitClient.carritoApiService.vaciarCarrito(usuarioId)
                _carrito.value = Carrito(id = 0L, estado = EstadoCarrito.ACTIVO, items = emptyList())
                _total.value = 0.0
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CarritoVM", "Error al vaciar carrito: ${e.message}")
            }
        }
    }

    /**
     * Finalizar la compra: notifica al backend y limpia el carrito local
     */
    fun finalizarCompra(
        usuarioId: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                RetrofitClient.carritoApiService.finalizarCompra(usuarioId)

                vaciarCarrito(usuarioId)

                onSuccess()

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CarritoVM", "Error al finalizar compra: ${e.message}")
            }
        }
    }


    /**
     * Calcula el total de los items del carrito
     */
    private fun calcularTotal(carrito: Carrito?) {
        _total.value = carrito?.items?.sumOf { it.precioUnitario * it.cantidad } ?: 0.0
    }
}