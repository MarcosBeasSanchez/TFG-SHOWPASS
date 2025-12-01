package com.example.appmovilshowpass.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilshowpass.data.remote.api.RetrofitClient
import com.example.appmovilshowpass.data.remote.dto.toEvento
import com.example.appmovilshowpass.model.Evento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar las búsquedas de eventos.
 * Utiliza StateFlow para exponer resultados de forma reactiva a la interfaz.
 */
class BusquedaViewModel : ViewModel() {

    // Flujo interno que contiene la lista de eventos resultante de la búsqueda.
    // MutableStateFlow permite modificar su valor dentro del ViewModel.
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())

    // Flujo inmutable expuesto a la UI.
    // Compose o cualquier observador debe usar esta referencia.
    val eventos: StateFlow<List<Evento>> = _eventos

    init {
        // No se realiza ninguna acción inicial.
        // El ViewModel espera a que se realicen búsquedas explícitas.
    }

    /**
     * Realiza una búsqueda de eventos por nombre mediante el servicio del backend.
     * La respuesta del servidor (DTOs) se convierte a objetos de dominio tipo Evento
     * y se publica en el StateFlow para actualizar la interfaz.
     */
    fun busquedaEventosPorNombre(nombre: String) {
        viewModelScope.launch {
            try {
                // Llamada a la API que devuelve una lista de DTOs.
                val listaDto = RetrofitClient.eventoApiService.obtenerEventoPorNombre(nombre)

                // Registro en log del tamaño de la respuesta.
                Log.d("BusquedaViewModel", "Eventos recibidos por nombre: ${listaDto.size}")

                // Conversión de DTO -> modelo de dominio y actualización del flujo.
                _eventos.value = listaDto.map { it.toEvento() }

            } catch (e: Exception) {
                // Se captura cualquier excepción de red o parseo.
                e.printStackTrace()
            }
        }
    }
}