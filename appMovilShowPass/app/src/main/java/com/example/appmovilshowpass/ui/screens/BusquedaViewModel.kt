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

class BusquedaViewModel : ViewModel() {
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    init {

    }
     fun busquedaEventosPorNombre(nombre: String) {
        viewModelScope.launch {
            try {
                val listaDto = RetrofitClient.eventoApiService.obtenerEventoPorNombre(nombre)
                Log.d("BusquedaViewModel", "Eventos recibidos por nombre: ${listaDto.size}")
                _eventos.value = listaDto.map { it.toEvento() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}