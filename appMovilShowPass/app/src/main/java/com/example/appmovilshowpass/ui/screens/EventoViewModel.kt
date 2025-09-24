package com.example.appmovilshowpass.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.model.Evento
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventoViewModel : ViewModel() {

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos
    private val allEventos = mutableListOf<Evento>()

    init {
        obtenerEventos()
        //actualizarEventosPeriodicamente()
    }

    // Función para obtener eventos desde la API y actualizar el StateFlow
    private fun obtenerEventos() {
        viewModelScope.launch {
            try {
                val listaDto = RetrofitClient.eventoApiService.obtenerTodosEventos()
                Log.d("EventoViewModel", "Eventos recibidos: ${listaDto.size}")
                _eventos.value = listaDto.map { it.toEvento() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
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
}