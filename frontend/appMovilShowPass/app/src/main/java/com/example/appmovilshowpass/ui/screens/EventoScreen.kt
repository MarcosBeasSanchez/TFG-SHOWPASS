package com.example.appmovilshowpass.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
// ... (otras importaciones)
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Importado para el tamaño de la cabecera
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appmovilshowpass.model.Categoria
import com.example.appmovilshowpass.ui.components.BarraCarga
import com.example.appmovilshowpass.ui.components.EventoCard
import com.example.appmovilshowpass.viewmodel.EventoViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal de visualización de eventos.
 *
 * Funcionalidad principal:
 * - Mostrar un listado de todos los eventos disponibles.
 * - Permitir filtrar por categorías mediante una fila de botones.
 * - Recargar los eventos utilizando el gesto Pull-to-Refresh.
 * - Navegar a la pantalla de detalle al pulsar sobre un evento.
 *
 * Estructura general de la pantalla:
 * 1. Obtención reactiva de los eventos desde el ViewModel mediante StateFlow.
 * 2. Barra de filtrado que permite seleccionar una categoría.
 * 3. Contenedor PullToRefreshBox que recarga la lista desde el backend.
 * 4. Listado de tarjetas de eventos con navegación a detalles.
 *
 * viewModel ViewModel que gestiona la carga, filtrado y refresco de eventos.
 * navController Controlador de navegación para abrir la pantalla de detalle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoScreen(
    viewModel: EventoViewModel = viewModel(),
    navController: NavController
) {
    // Recolección del flujo de eventos generado por el ViewModel.
    val eventos by viewModel.eventos.collectAsState()

    // Lista de categorías generadas dinámicamente: "TODOS" + todas las existentes.
    val eventosBoton = listOf("TODOS") + Categoria.values().map { it.name }

    // Estado que controla el gesto Pull-to-Refresh.
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Estado interno que recuerda la categoría seleccionada actualmente.
    var categoriaSeleccionada by remember { mutableStateOf("TODOS") }


    /**
     * Contenedor que habilita el gesto Pull-to-Refresh, permitiendo
     * refrescar los eventos directamente desde la API.
     */
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            scope.launch {
                viewModel.obtenerEventosSuspend()   // Carga suspendida desde backend
                isRefreshing = false
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {

        /**
         * Si la lista de eventos aún no está cargada o está vacía,
         * se muestra una barra de carga.
         */
        if (eventos.isEmpty()) {
            BarraCarga(modifier = Modifier.fillMaxSize())

        } else {

            /**
             * Listado principal que contiene:
             * - Barra horizontal de categorías.
             * - Lista vertical de tarjetas de eventos.
             */
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {

                /**
                 * BARRA DE FILTRADO POR CATEGORÍAS
                 *
                 * Compuesta por botones horizontales.
                 * Cada botón actualiza la categoría seleccionada
                 * y lanza un filtrado en el ViewModel.
                 */
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(eventosBoton) { boton ->

                            val estaSeleccionado = boton == categoriaSeleccionada

                            // Estilo visual condicionado según si está seleccionado o no.
                            val buttonColors = if (estaSeleccionado) {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    categoriaSeleccionada = boton
                                    viewModel.filtrarEventosPorCategoria(boton)
                                },
                                modifier = Modifier.padding(3.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = buttonColors
                            ) {
                                Text(boton, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }


                /**
                 * CABECERA OPCIONAL
                 *
                 * Cuando el usuario selecciona una categoría distinta a "TODOS",
                 * se muestra como título encima del listado.
                 */
                if (categoriaSeleccionada != "TODOS") {
                    item {
                        Text(
                            text = categoriaSeleccionada,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }


                /**
                 * LISTADO DE EVENTOS
                 *
                 * Cada evento se muestra mediante una tarjeta reutilizable
                 * que recibe el NavController para permitir navegación al detalle.
                 */
                items(eventos, key = { it.id }) { evento ->
                    EventoCard(evento, navController = navController)
                }
            }
        }
    }
}