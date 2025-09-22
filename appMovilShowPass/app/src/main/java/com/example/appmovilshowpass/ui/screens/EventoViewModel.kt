package com.example.appmovilshowpass.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventoViewModel : ViewModel() {

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    //Al iniciar el ViewModel, se llama a obtenerEventos para cargar los datos
    init {
        obtenerEventos()
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
}