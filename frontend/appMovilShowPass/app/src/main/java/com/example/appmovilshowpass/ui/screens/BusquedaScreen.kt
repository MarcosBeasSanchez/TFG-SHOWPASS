package com.example.appmovilshowpass.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovilshowpass.ui.components.Cabecera
import com.example.appmovilshowpass.ui.components.SearchBarBusqueda
import com.example.appmovilshowpass.viewmodel.BusquedaViewModel


/**
 * Pantalla principal del módulo de búsqueda de eventos.
 *
 * Esta pantalla actúa como contenedor y delega la lógica de búsqueda en:
 * - El BusquedaViewModel, que gestiona las consultas al backend y expone los resultados.
 * - El composable SearchBarBusqueda, que contiene la interfaz completa de búsqueda
 *   (entrada de texto, banner inicial, filtrado dinámico y listado de resultados).
 *
 * viewModel ViewModel encargado de obtener los eventos filtrados desde la API.
 * navController Controlador de navegación utilizado para acceder al detalle de un evento.
 */
@Composable
fun BusquedaScreen(
    viewModel: BusquedaViewModel = viewModel(),
    navController: NavController
) {
    // Observa de manera reactiva los resultados de búsqueda.
    val eventos by viewModel.eventos.collectAsState()

    Column(
        modifier = Modifier.padding(vertical = 0.dp, horizontal = 16.dp)
    ) {

        // Cabecera informativa de la pantalla.
        Cabecera("Buscar Eventos", Icons.Default.Search)

        /**
         * Composable que contiene todo el flujo de búsqueda:
         * - Entrada de texto
         * - Ejecución de la búsqueda
         * - Visualización de resultados
         * - Navegación al detalle de cada evento.
         */
        SearchBarBusqueda(
            eventos = eventos,
            viewModel = viewModel,
            navController = navController
        )
    }
}

