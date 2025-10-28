package com.example.appmovilshowpass.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.DTOeventoSubida
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventoViewModel : ViewModel() {

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos
    private val allEventos = mutableListOf<Evento>()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> get() = _mensaje


    init {
        obtenerEventos()
    }

    // Función para obtener eventos desde la API y actualizar el StateFlow
    fun obtenerEventos() {
        viewModelScope.launch {
            try {
                val listaDto = RetrofitClient.eventoApiService.obtenerTodosEventos()
                Log.d("EventoViewModel", "Eventos recibidos: ${listaDto.size}")
                // Convertir DTOs a modelos de dominio y actualizar el StateFlow
                _eventos.value = listaDto.map { it.toEvento() }.shuffled()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Versión suspendida de la función para llamadas desde corrutinas
    suspend fun obtenerEventosSuspend() {
        try {
            val listaDto = RetrofitClient.eventoApiService.obtenerTodosEventos()
            _eventos.value = listaDto.map { it.toEvento() }.shuffled()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // Función para filtrar eventos por categoría
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

    // Función que hace polling cada 5 segundos
    private fun actualizarEventosPeriodicamente() {
        viewModelScope.launch {
            while (true) { //bucle infinito
                obtenerEventos()
                delay(5000) // espera 5 segundos
            }
        }
    }

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

}