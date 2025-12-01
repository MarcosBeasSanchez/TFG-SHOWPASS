package com.example.appmovilshowpass.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOEventoRecomendado
import com.example.appmovilshowpass.data.remote.dto.DTOeventoSubida
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar toda la lógica relacionada con eventos:
 * - Obtener eventos desde el backend
 * - Filtrar por categoría
 * - Crear y actualizar eventos
 * - Obtener eventos de un vendedor específico
 * - Recomendaciones basadas en otros eventos o en el usuario
 *
 * Utiliza StateFlow para exponer los datos de forma reactiva a la interfaz.
 */
class EventoViewModel : ViewModel() {

    // Lista de eventos disponibles en la aplicación.
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    // Lista auxiliar para posibles filtrados locales (no se utiliza actualmente).
    private val allEventos = mutableListOf<Evento>()

    // Indica si una operación larga (petición, creación, actualización) está en ejecución.
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    // Contiene mensajes informativos o de error para la interfaz.
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> get() = _mensaje

    // Lista de eventos recomendados obtenidos desde el backend.
    private val _recomendados = MutableStateFlow<List<DTOEventoRecomendado>>(emptyList())
    val recomendados: StateFlow<List<DTOEventoRecomendado>> get() = _recomendados


    init {
        // Al iniciar el ViewModel se obtienen todos los eventos.
        obtenerEventos()
    }

    /**
     * Obtiene todos los eventos desde el backend y actualiza el StateFlow.
     * Los eventos se mezclan aleatoriamente para variar su orden en la UI.
     */
    fun obtenerEventos() {
        viewModelScope.launch {
            try {
                val listaDto = RetrofitClient.eventoApiService.obtenerTodosEventos()
                Log.d("EventoViewModel", "Eventos recibidos: ${listaDto.size}")

                _eventos.value = listaDto.map { it.toEvento() }.shuffled()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Versión suspendida de obtenerEventos(), pensada para ser llamada
     * desde corrutinas externas sin necesidad de viewModelScope.
     */
    suspend fun obtenerEventosSuspend() {
        try {
            val listaDto = RetrofitClient.eventoApiService.obtenerTodosEventos()
            _eventos.value = listaDto.map { it.toEvento() }.shuffled()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Filtra los eventos dependiendo de la categoría seleccionada.
     * Si la categoría es "TODOS", vuelve a cargar todos los eventos.
     */
    fun filtrarEventosPorCategoria(categoria: String) {
        viewModelScope.launch {
            try {
                val response = if (categoria == "TODOS") {
                    RetrofitClient.eventoApiService.obtenerTodosEventos()
                } else {
                    RetrofitClient.eventoApiService.findByCategoria(categoria)
                }

                _eventos.value = response.map { it.toEvento() }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Método privado que ejecuta un bucle infinito para refrescar los eventos cada 5 segundos.
     * Esta función no se está utilizando actualmente, pero queda disponible para implementación futura.
     */
    private fun actualizarEventosPeriodicamente() {
        viewModelScope.launch {
            while (true) {
                obtenerEventos()
                delay(5000)
            }
        }
    }

    /**
     * Envía al backend la creación de un nuevo evento.
     * Muestra un mensaje en caso de éxito o error.
     */
    fun crearEvento(dto: DTOeventoSubida, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true

                RetrofitClient.eventoApiService.crearEvento(dto)

                _mensaje.value = " Evento creado correctamente"
                onSuccess()

            } catch (e: Exception) {
                e.printStackTrace()
                _mensaje.value = " Error al crear evento"

            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Obtiene del backend los eventos asociados a un vendedor específico.
     */
    fun obtenerEventosDeVendedor(idVendedor: Long) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val lista = RetrofitClient.eventoApiService.getEventosByVendedor(idVendedor)
                _eventos.value = lista.map { it.toEvento() }

            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Actualiza un evento existente en el backend.
     * Si la operación tiene éxito, envía un mensaje informativo.
     */
    fun actualizarEvento(id: Long, dto: DTOeventoSubida, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true

                RetrofitClient.eventoApiService.actualizarEvento(id, dto)

                _mensaje.value = " Evento actualizado correctamente"
                onSuccess()

            } catch (e: Exception) {
                e.printStackTrace()
                _mensaje.value = " Error al actualizar evento"

            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Solicita recomendaciones de eventos basadas en un evento concreto.
     */
    fun recomendarPorEvento(eventoId: Long) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val listaRecomendada =
                    RetrofitClient.eventoApiService.obtenerRecomendacioPorEvento(eventoId)

                _recomendados.value = listaRecomendada

            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Solicita recomendaciones personalizadas según el historial del usuario.
     */
    fun recomendarPorUsuario(userId: Long) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val listaRecomendada =
                    RetrofitClient.eventoApiService.obtenerRecomendacioPorUsuario(userId)

                _recomendados.value = listaRecomendada

            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
                _loading.value = false
            }
        }
    }
}