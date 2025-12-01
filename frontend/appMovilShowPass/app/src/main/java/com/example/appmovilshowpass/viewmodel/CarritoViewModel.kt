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



/**
 * ViewModel encargado de gestionar el carrito de compra del usuario.
 * Mantiene el estado reactivo del carrito, el total calculado y los eventos comprados.
 * Todas las operaciones que modifican el carrito se sincronizan con el backend.
 */
class CarritoViewModel : ViewModel() {

    // Estado del carrito actual. Puede ser nulo hasta que se cargue desde el servidor.
    private val _carrito = MutableStateFlow<Carrito?>(null)
    val carrito: StateFlow<Carrito?> get() = _carrito

    // Total acumulado del carrito, calculado automáticamente cuando se modifica el carrito.
    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> get() = _total

    // Lista de eventos comprados. Solo se usa si se requiere mostrar un historial o confirmación.
    private val _eventosComprados = MutableStateFlow<List<Evento>>(emptyList())
    val eventosComprados: StateFlow<List<Evento>> get() = _eventosComprados

    /**
     * Carga el carrito del usuario desde el backend.
     * Convierte el DTO recibido a modelo Carrito y actualiza el total.
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
     * Agrega un evento al carrito indicando cantidad.
     * El backend devuelve el carrito actualizado tras agregar el item.
     */
    fun agregarItem(usuarioId: Long, eventoId: Long, cantidad: Int = 1) {
        viewModelScope.launch {
            try {
                Log.d("CarritoVM", "Agregando item: user=$usuarioId evento=$eventoId cantidad=$cantidad")

                // Cuerpo enviado al backend. Solo se envía la cantidad.
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
     * Elimina un item del carrito según su ID.
     * El backend devuelve nuevamente el carrito actualizado.
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
     * Vacía completamente el carrito del usuario.
     * Tras la llamada al backend, también se limpia el estado local y el total.
     */
    fun vaciarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            try {
                Log.d("CarritoVM", "Vaciando carrito de usuario $usuarioId")

                RetrofitClient.carritoApiService.vaciarCarrito(usuarioId)

                // Se inicializa el carrito a un estado vacío.
                _carrito.value = Carrito(
                    id = 0L,
                    estado = EstadoCarrito.ACTIVO,
                    items = emptyList()
                )

                _total.value = 0.0

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CarritoVM", "Error al vaciar carrito: ${e.message}")
            }
        }
    }

    /**
     * Finaliza la compra del usuario.
     * Primero se notifica al backend, luego se vacía el carrito local
     * y finalmente se ejecuta la acción de éxito proporcionada por la UI.
     */
    fun finalizarCompra(
        usuarioId: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Notifica al backend para generar la compra y los tickets.
                RetrofitClient.carritoApiService.finalizarCompra(usuarioId)

                // Limpia el carrito local.
                vaciarCarrito(usuarioId)

                // Notificación a la UI para proceder (por ejemplo navegar o mostrar mensaje).
                onSuccess()

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CarritoVM", "Error al finalizar compra: ${e.message}")
            }
        }
    }

    /**
     * Calcula el total del carrito sumando precioUnitario * cantidad.
     * Si el carrito es nulo, el total pasa a 0.
     */
    private fun calcularTotal(carrito: Carrito?) {
        _total.value = carrito?.items?.sumOf { it.precioUnitario * it.cantidad } ?: 0.0
    }
}